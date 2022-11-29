package docSharing.service;

import docSharing.test.ManipulatedText;
import docSharing.test.PrepareDocumentLog;
import docSharing.test.UpdateType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogService {
    private static final Logger logger = LogManager.getLogger(LogService.class.getName());
    static Map<Long, PrepareDocumentLog> userLogByDocIdMap = new HashMap<>();


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
//                    rightPlace = i - 1;
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
        for (int i = manipulatedText.getStartPosition() + 1; i < docLog.getIndex().size(); i++) {
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
        makeCorrectionToDelete(docId, manipulatedText);
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
}
