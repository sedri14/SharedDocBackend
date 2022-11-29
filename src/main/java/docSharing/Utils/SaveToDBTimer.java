package docSharing.Utils;

import docSharing.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.TimerTask;

public class SaveToDBTimer extends TimerTask {
    Map<Long, String> docContentByDocId;
    @Autowired
    DocService docService;

    public SaveToDBTimer(Map<Long, String> docContentByDocId) {
        this.docContentByDocId = docContentByDocId;
    }

    @Override
    public void run() {

        updateAllActiveDocsToDB();
    }

    private void updateAllActiveDocsToDB() {
        for (Map.Entry<Long, String> document : docContentByDocId.entrySet()) {
            Long key = document.getKey();
            String content = document.getValue();
            docService.saveContentToDB(key, content);
        }
    }

}
