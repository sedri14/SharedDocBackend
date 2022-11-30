package docSharing.service;

import docSharing.entities.Document;
import docSharing.entities.Log;
import docSharing.entities.User;
import docSharing.repository.LogRepository;
import docSharing.test.ManipulatedText;
import docSharing.test.PrepareDocumentLog;
import docSharing.test.UpdateType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    static Map<Long, PrepareDocumentLog> userLogByDocIdMap = new HashMap<>();

    public LogService() {
        Runnable saveContentToDBRunnable = new Runnable() {
            public void run() {
//                saveLogToDB("khader", 2L, 4L);

            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(saveContentToDBRunnable, 0, 5, TimeUnit.SECONDS);
    }

    public void addToLog(Long docId, ManipulatedText manipulatedText) {
        if (!userLogByDocIdMap.containsKey(docId)) {
            userLogByDocIdMap.put(docId, new PrepareDocumentLog());
        }
        switch (manipulatedText.getType()) {
            case APPEND:
                appendContentToLog(docId, manipulatedText);
                break;
            case DELETE:
                deleteContentToLog(docId, manipulatedText);
                break;
            case DELETE_RANGE:
                deleteRangeContentToLog(docId, manipulatedText);
                break;
//            case APPEND_RANGE:
//                appendRangeContentToLog(docId, manipulatedText);
//                break;

        }

    }

    private static void appendContentToLog(Long docId, ManipulatedText manipulatedText) {
        insertIntoListAppend(
                manipulatedText.getStartPosition()
                , manipulatedText.getContent()
                , "A"
                , 1L
                , userLogByDocIdMap.get(docId).getContent()
                , userLogByDocIdMap.get(docId).getIndex()
                , userLogByDocIdMap.get(docId).getAction()
                , userLogByDocIdMap.get(docId).getUserId()
        );
        logger.info("====================================");
        logger.info("added one char");
        logger.info(userLogByDocIdMap.get(docId));
        logger.info("correction is done");
        makeCorrectionToAppending(docId, manipulatedText);
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

    private static void makeCorrectionToAppending(Long docId, ManipulatedText manipulatedText) {

        PrepareDocumentLog docLog = userLogByDocIdMap.get(docId);
        int rightIndex = 0;
        for (int j = 0; j < docLog.getIndex().size(); j++) {
            if (docLog.getIndex().get(j) >= manipulatedText.getStartPosition()) {
                rightIndex = j;
                break;
            }

        }
        logger.info("the right place to start is" + rightIndex);
        for (int i = rightIndex + 1; i < docLog.getIndex().size(); i++) {
            if (docLog.getIndex().get(i) >= manipulatedText.getStartPosition()) {
                docLog.getIndex().set(i, docLog.getIndex().get(i) + 1);
            }

        }
        logger.info("after correction");
        logger.info(userLogByDocIdMap.get(docId));
    }


    private static void deleteContentToLog(Long docId, ManipulatedText manipulatedText) {

        insertIntoListDelete(
                manipulatedText.getStartPosition()
                , manipulatedText.getContent()
                , "D"
                , 1L
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

    private static void makeCorrectionToDelete(Long docId, ManipulatedText manipulatedText) {
        PrepareDocumentLog docLog = userLogByDocIdMap.get(docId);
        int rightIndex = 0;
        for (int j = 0; j < docLog.getIndex().size(); j++) {
            if (docLog.getIndex().get(j) >= manipulatedText.getStartPosition()) {
                rightIndex = j;
                break;
            }

        }
        logger.info("the right place to start is" + rightIndex);
//        docLog.getIndex().remove(rightIndex);
//        docLog.getUserId().remove(rightIndex);
//        docLog.getAction().remove(rightIndex);
//        docLog.getContent().remove(rightIndex);
        for (int i = rightIndex + 1; i < docLog.getIndex().size(); i++) {
            if (docLog.getIndex().get(i) > manipulatedText.getStartPosition()) {
                docLog.getIndex().set(i, docLog.getIndex().get(i) - 1);
            }

        }


        for (Map.Entry<Long, PrepareDocumentLog> entry : userLogByDocIdMap.entrySet()) {
            PrepareDocumentLog value = entry.getValue();

            for (int i = value.getIndex().indexOf(manipulatedText.getStartPosition()) + 1; i < value.getIndex().size(); i++) {
                value.getIndex().set(i, value.getIndex().get(i) - 1);
            }
        }
        logger.info("after correction");
        logger.info(userLogByDocIdMap.get(docId));
    }


    private static void deleteRangeContentToLog(Long docId, ManipulatedText manipulatedText) {
        for (int i = 0; i < manipulatedText.getContent().length(); i++) {
            deleteContentToLog(docId, new ManipulatedText(
                    manipulatedText.getUser()
                    , UpdateType.DELETE
                    , manipulatedText.getContent().substring(i, i + 1)
                    , manipulatedText.getStartPosition() + i + 1
                    , manipulatedText.getStartPosition() + i + 1)
            );
        }
//        for (int i = 0; i < manipulatedText.getContent().length(); i++) {
//            userLogByDocIdMap.get(docId).getContent().add(manipulatedText.getContent().substring(i, i + 1));
//            userLogByDocIdMap.get(docId).getIndex().add(manipulatedText.getStartPosition() + i);
//            userLogByDocIdMap.get(docId).getAction().add("A");
//        }
//    }
//
//}

//    private static void appendRangeContentToLog(Long docId, ManipulatedText manipulatedText) {
//        for (int i = 0; i < manipulatedText.getContent().length(); i++) {
//            appendContentToLog(docId, new ManipulatedText(
//                    manipulatedText.getUser()
//                    , UpdateType.APPEND
//                    , manipulatedText.getContent().substring(i, i + 1)
//                    , manipulatedText.getStartPosition() + i
//                    , manipulatedText.getStartPosition() + i)
//            );
//        }
//        for (int i = 0; i < manipulatedText.getContent().length(); i++) {
//            userLogByDocIdMap.get(docId).getContent().add(manipulatedText.getContent().substring(i, i + 1));
//            userLogByDocIdMap.get(docId).getIndex().add(manipulatedText.getStartPosition() + i);
//            userLogByDocIdMap.get(docId).getAction().add("A");
//        }
//
//    }
//
//    private static void makeCorrectionToDelete(Long docId, ManipulatedText manipulatedText) {
//        for (Map.Entry<Long, UserLogByDoc> entry : userLogByDocIdMap.entrySet()) {
//            UserLogByDoc value = entry.getValue();
////            if ((String) value.getUserId() == manipulatedText.getUser()) {//this one should be long and by the id and not by the email
////                  continue;
////            }
//            if (value.getIndex().get(value.getIndex().size()) < manipulatedText.getStartPosition()) {
//                for (int i = manipulatedText.getStartPosition(); i < value.getIndex().size(); i++) {
//                    value.getIndex().set(i, value.getIndex().get(i) + 1);
//                }
//            }
//
//        }
//    }

    }

    private void saveAllLogsToDB() {


        for (Map.Entry<Long, PrepareDocumentLog> docLog : userLogByDocIdMap.entrySet()) {

            String tempContent = docLog.getValue().getContent().get(0);
            int tempIndex = docLog.getValue().getIndex().get(0);
            Long tempUser = docLog.getValue().getUserId().get(0);

            for (int i = 1; i < docLog.getValue().getIndex().size() - 1; i++) {

                if (
                        tempIndex + 1 == docLog.getValue().getIndex().get(i)
                                && tempUser == docLog.getValue().getUserId().get(i)
                ) {
                    tempContent += docLog.getValue().getContent().get(i);
                    tempIndex = docLog.getValue().getIndex().get(i);
                    tempUser = docLog.getValue().getUserId().get(i);
                } else {
//                    Log log = new Log();
//                    saveOneLogToDB(tempContent, tempUser, docLog.getKey());
                    tempContent = "";
                    tempIndex = docLog.getValue().getIndex().get(i);
                    tempUser = docLog.getValue().getUserId().get(i);
                }
            }
            userLogByDocIdMap.clear();
        }
    }

    private void saveOneLogToDB(String logContent, User user, Document doc) {
        Log log = new Log(user, doc, logContent, LocalDateTime.now());
        logRepository.save(log);
    }

//    private void saveLog(PrepareDocumentLog allLog) {
//        String content = "";
//
//        for (int i = 0; i < allLog.getContent().size(); i++) {
////            if (allLog.getIndex().get(i) + allLog.getIndex().get() )
//
//        }
//
//
//    }
}