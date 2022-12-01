package docSharing.service;

import docSharing.entities.Log;
import docSharing.repository.LogRepository;
import docSharing.DTO.Doc.ManipulatedTextDTO;
import docSharing.DTO.Doc.PrepareDocumentLogDTO;
import docSharing.DTO.Doc.UpdateTypeDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LogService {
    @Autowired
    UserService userService;
    @Autowired
    LogRepository logRepository;
    private static final Logger logger = LogManager.getLogger(LogService.class.getName());
    static Map<Long, PrepareDocumentLogDTO> userLogByDocIdMap = new HashMap<>();

    public LogService() {
        Runnable saveContentToDBRunnable = new Runnable() {
            public void run() {
                saveAllLogsToDB();
//                logger.info("the log is saved");
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(saveContentToDBRunnable, 0, 10, TimeUnit.SECONDS);
    }

    public void addToLog(Long docId, ManipulatedTextDTO manipulatedTextDTO) {
        if (!userLogByDocIdMap.containsKey(docId)) {
            userLogByDocIdMap.put(docId, new PrepareDocumentLogDTO());
        }
        switch (manipulatedTextDTO.getAction()) {
            case APPEND:
                appendContentToLog(docId, manipulatedTextDTO);
                break;
            case DELETE:
                deleteContentToLog(docId, manipulatedTextDTO);
                break;
            case DELETE_RANGE:
                deleteRangeContentToLog(docId, manipulatedTextDTO);
                break;
            case APPEND_RANGE:
                appendRangeContentToLog(docId, manipulatedTextDTO);
                break;

        }

    }

    private static void appendContentToLog(Long docId, ManipulatedTextDTO manipulatedTextDTO) {
        insertIntoListAppend(
                manipulatedTextDTO.getStartPosition()
                , manipulatedTextDTO.getContent()
                , "A"
                , manipulatedTextDTO.userId
                , userLogByDocIdMap.get(docId).getContent()
                , userLogByDocIdMap.get(docId).getIndex()
                , userLogByDocIdMap.get(docId).getAction()
                , userLogByDocIdMap.get(docId).getUserId()
        );
        logger.info("====================================");
        logger.info("added one char");
        logger.info(userLogByDocIdMap.get(docId));
        logger.info("correction is done");
        makeCorrectionToAppending(docId, manipulatedTextDTO);
        logger.info("====================================");

    }

    private static void insertIntoListAppend(int pointerPosition, String content, String action, Long userId, List<String> contentList, List<Integer> indexList, List<String> actionList, List<Long> userIdList) {
        int rightPlace = indexList.size();
        for (int i = 0; i < indexList.size(); i++) {
            if (indexList.get(i) >= pointerPosition) {
                if (i == 0) {
                    rightPlace = 0;
                } else {
                    rightPlace = i;
                }
                break;
            }
        }
        contentList.add(rightPlace, content);
        indexList.add(rightPlace, pointerPosition);
        actionList.add(rightPlace, action);
        userIdList.add(rightPlace, userId);

    }

    private static void makeCorrectionToAppending(Long docId, ManipulatedTextDTO manipulatedTextDTO) {

        PrepareDocumentLogDTO docLog = userLogByDocIdMap.get(docId);
        int rightIndex = 0;
        for (int j = 0; j < docLog.getIndex().size(); j++) {
            if (docLog.getIndex().get(j) >= manipulatedTextDTO.getStartPosition()) {
                rightIndex = j;
                break;
            }

        }
        logger.info("the right place to start is" + rightIndex);
        for (int i = rightIndex + 1; i < docLog.getIndex().size(); i++) {
            if (docLog.getIndex().get(i) >= manipulatedTextDTO.getStartPosition()) {
                docLog.getIndex().set(i, docLog.getIndex().get(i) + 1);
            }

        }
        logger.info("after correction");
        logger.info(userLogByDocIdMap.get(docId));
    }


    private static void deleteContentToLog(Long docId, ManipulatedTextDTO manipulatedTextDTO) {

        insertIntoListDelete(
                manipulatedTextDTO.getStartPosition()
                , manipulatedTextDTO.getContent()
                , "D"
                , manipulatedTextDTO.userId
                , userLogByDocIdMap.get(docId).getContent()
                , userLogByDocIdMap.get(docId).getIndex()
                , userLogByDocIdMap.get(docId).getAction()
                , userLogByDocIdMap.get(docId).getUserId()
        );
        logger.info("====================================");
        logger.info("Deleted one char");
        logger.info(userLogByDocIdMap.get(docId));
//        makeCorrectionToDelete(docId, manipulatedText);
        logger.info("correction is done");
        logger.info("====================================");

    }

    private static void insertIntoListDelete(int pointerPosition, String content, String action, Long userId, List<String> contentList, List<Integer> indexList, List<String> actionList, List<Long> userIdList) {
        int rightPlace = indexList.size();
        contentList.add(rightPlace, content);
        indexList.add(rightPlace, pointerPosition);
        actionList.add(rightPlace, action);
        userIdList.add(rightPlace, userId);

    }

    private static void deleteRangeContentToLog(Long docId, ManipulatedTextDTO manipulatedTextDTO) {
        for (int i = 0; i < manipulatedTextDTO.getContent().length(); i++) {

            ManipulatedTextDTO contentSlice = new ManipulatedTextDTO(
                    manipulatedTextDTO.userId
                    , manipulatedTextDTO.action
                    , manipulatedTextDTO.getContent().substring(i, i + 1)
                    , manipulatedTextDTO.startPosition
                    , manipulatedTextDTO.endPosition);

            deleteContentToLog(docId, contentSlice);
        }
    }

    private static void appendRangeContentToLog(Long docId, ManipulatedTextDTO manipulatedTextDTO) {
        for (int i = 0; i < manipulatedTextDTO.getContent().length(); i++) {

            ManipulatedTextDTO contentSlice = new ManipulatedTextDTO(
                    manipulatedTextDTO.userId
                    , manipulatedTextDTO.action
                    , manipulatedTextDTO.getContent().substring(i, i + 1)
                    , manipulatedTextDTO.startPosition
                    , manipulatedTextDTO.endPosition);

            appendContentToLog(docId, contentSlice);

        }

    }

    private void saveAllLogsToDB() {


        for (Map.Entry<Long, PrepareDocumentLogDTO> docLog : userLogByDocIdMap.entrySet()) {


            String tempContent = docLog.getValue().getContent().get(0);
            int tempIndex = docLog.getValue().getIndex().get(0);
            Long tempUser = docLog.getValue().getUserId().get(0);
            String action = docLog.getValue().getAction().get(0);

            if (docLog.getValue().getIndex().size() == 1) {
                saveOneLogToDB(tempContent, action, tempUser, docLog.getKey());
                break;
            }

            for (int i = 1; i < docLog.getValue().getIndex().size(); i++) {

                if (tempIndex + 1 == docLog.getValue().getIndex().get(i)
                        && Objects.equals(tempUser, docLog.getValue().getUserId().get(i))
                        && action.equals(docLog.getValue().getAction().get(i))
                ) {
                    tempContent += docLog.getValue().getContent().get(i);
                    tempIndex = docLog.getValue().getIndex().get(i);
                    tempUser = docLog.getValue().getUserId().get(i);
                    action = docLog.getValue().getAction().get(i);

                    if (i == docLog.getValue().getIndex().size() - 1) {
                        saveOneLogToDB(tempContent, action, tempUser, docLog.getKey());
                    }
                } else {
                    saveOneLogToDB(tempContent, action, tempUser, docLog.getKey());
                    tempContent = docLog.getValue().getContent().get(i);
                    tempIndex = docLog.getValue().getIndex().get(i);
                    tempUser = docLog.getValue().getUserId().get(i);
                    action = docLog.getValue().getAction().get(i);
                }
//                Log log = new Log(tempUser, docLog.getKey(), tempContent, LocalDateTime.now());
//                logger.warn("save to log the " + log);
            }
            userLogByDocIdMap.clear();
        }
    }

    public void saveOneLogToDB(String logContent, String action, Long userId, Long docId) {
        Log log = new Log(userId, docId, logContent, action, LocalDateTime.now());
        logger.info("the log object is " + log);
        logRepository.save(log);
    }


//    private static void makeCorrectionToDelete(Long docId, ManipulatedTextDTO manipulatedTextDTO) {
//        PrepareDocumentLogDTO docLog = userLogByDocIdMap.get(docId);
//        int rightIndex = 0;
//        for (int j = 0; j < docLog.getIndex().size(); j++) {
//            if (docLog.getIndex().get(j) >= manipulatedTextDTO.getStartPosition()) {
//                rightIndex = j;
//                break;
//            }
//
//        }
//        logger.info("the right place to start is" + rightIndex);
////        docLog.getIndex().remove(rightIndex);
////        docLog.getUserId().remove(rightIndex);
////        docLog.getAction().remove(rightIndex);
////        docLog.getContent().remove(rightIndex);
//        for (int i = rightIndex + 1; i < docLog.getIndex().size(); i++) {
//            if (docLog.getIndex().get(i) > manipulatedTextDTO.getStartPosition()) {
//                docLog.getIndex().set(i, docLog.getIndex().get(i) - 1);
//            }
//
//        }
//
//
//        for (Map.Entry<Long, PrepareDocumentLogDTO> entry : userLogByDocIdMap.entrySet()) {
//            PrepareDocumentLogDTO value = entry.getValue();
//
//            for (int i = value.getIndex().indexOf(manipulatedTextDTO.getStartPosition()) + 1; i < value.getIndex().size(); i++) {
//                value.getIndex().set(i, value.getIndex().get(i) - 1);
//            }
//        }
//        logger.info("after correction");
//        logger.info(userLogByDocIdMap.get(docId));
//    }

}