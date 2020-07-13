import javax.crypto.KeyGenerator;
import java.io.*;
import java.security.*;
import java.util.Scanner;
import javax.crypto.*;

public class EncodeAllData {

    private static MySynchronizedList list = new MySynchronizedList();
    private static File inputFile = new File("inputFile.txt");

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        SecretKey key = keygen.generateKey();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("key.txt"))) {
            out.writeObject(key);
        }

        Runnable addDataToList = () -> {
            try (Scanner reading = new Scanner(inputFile)) {
                while (reading.hasNext()) {
                    String line = reading.nextLine();
                    list.addDataToList(line);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
        // 2 wątki
        new Thread(addDataToList).start();
        new Thread(addDataToList).start();

        Runnable encodeDataInMyList = () -> {
            DataAndIndex dataAndIndex = list.takeFirstDecoded();
            ByteArrayInputStream in = new ByteArrayInputStream(dataAndIndex.getData().getBytes());
            ByteArrayOutputStream out = new ByteArrayOutputStream(dataAndIndex.getData().getBytes().length);
            try {
                encrypt(in, out);
            } catch (IOException | GeneralSecurityException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            list.saveEncodedData(new DataAndIndex(out.toString(), dataAndIndex.getArrayIndex()));
        };
        // 1 wątki
        new Thread(encodeDataInMyList).start();

//        Runnable ptintList = () -> {
////            System.out.println("Data: " + list.getData().stream() + "status: " + list.getStatus().stream() );
//            System.out.println("Data: " + Arrays.toString(list.getData().toArray()) + "status: " + Arrays.toString(list.getStatus().toArray()) );
//        };

        // 1 wątek
//        new Thread(ptintList).start();
    }

    private static void encrypt(InputStream in, ByteArrayOutputStream out) throws IOException, ClassNotFoundException, GeneralSecurityException {
        int mode = Cipher.ENCRYPT_MODE;

        try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream("key.txt"));) {
            Key key = (Key) keyIn.readObject();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, key);
            Util.crypt(in, out, cipher);
        }
    }

}