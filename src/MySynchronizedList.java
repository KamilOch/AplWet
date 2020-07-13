import java.util.ArrayList;
import java.util.List;

public class MySynchronizedList {

    List<String> data = new ArrayList<>();
    List<Status> status = new ArrayList<>();

    synchronized void addDataToList(String inputData) {
        data.add(inputData);
        status.add(Status.DECOCED);
    }

    synchronized DataAndIndex takeFirstDecoded() {
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


    synchronized void saveEncodedData(DataAndIndex dataAndIndex) {
        int index = dataAndIndex.getArrayIndex();
        data.set(index, dataAndIndex.getData());
        status.set(index, Status.ENCODED);
    }

    synchronized boolean checkIfAllDataAreEncoded() {
        boolean flag = true;
        for (int i = 0; i < status.size(); i++) {
            if (status.get(i) == Status.DECOCED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    synchronized void print() {
        int x = data.size();
        for (int i = 0; i < data.size(); i++
        ) {
            System.out.println("Data: " + data.get(i) + " Status: " + status.get(i));
        }
    }

}


