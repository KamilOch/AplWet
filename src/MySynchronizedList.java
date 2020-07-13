import java.util.ArrayList;
import java.util.List;

public class MySynchronizedList {

    List<String> data = new ArrayList<>();
    List<Status> status = new ArrayList<>();

    void addDataToList(String inputData) {
        synchronized (MySynchronizedList.class) {
            data.add(inputData);
            status.add(Status.DECOCED);
        }
    }

    DataAndIndex takeFirstDecoded() {
        synchronized (MySynchronizedList.class) {
            String firstDecodecodData = "";
            int index = 0;
            for (int i = 0; i < status.size(); i++) {
                if (status.get(i) == Status.DECOCED) {
                    status.set(i, Status.INWORK);
                    firstDecodecodData = data.get(i);
                    index = i;
                    break;
                }
            }
            return new DataAndIndex(firstDecodecodData, index);
        }
    }

    void saveEncodedData(DataAndIndex dataAndIndex) {
        synchronized (MySynchronizedList.class) {
            int index = dataAndIndex.getArrayIndex();
            data.set(index, dataAndIndex.getData());
            status.set(index, Status.ENCODED);
        }
    }

    boolean checkIfAllDataAreEncoded() {
        boolean flag = true;

        for (int i = 0; i < status.size(); i++) {
            if (status.get(i) == Status.DECOCED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    void print() {
        int x = data.size();
        for (int i = 0; i < data.size(); i++
        ) {
            System.out.println("Data: " + data.get(i) + " Status: " +status.get(i));
        }

    }
}


