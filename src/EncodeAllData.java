import javax.crypto.KeyGenerator;
import java.io.*;
import java.security.*;
import java.util.Scanner;
import javax.crypto.*;

/*
program który pobiera plik.txt
dodaje go do synchronizowanej listy (własna implementacja synchronizowanej listy)
następnie uruchamiam 3 wątki szyfrujące które pobierają z listy dane,
szyfrują je i zapisują w tym samym miejscu na lisice gdzie były dane nie zaszyfrowane.
wątki sprawdzają czy na liście znajdują się jeszcze jakieś dane nie zaszyfrowane jeśli tak
to pobierają pierwsze jakie znajdą do szyfrowania a jeśli wszystkie dane zostały zaszyfrowane kończy prace.

Wynik pracy programu można ogladać w konsoli.
 */

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

        try (Scanner reading = new Scanner(inputFile)) {
            while (reading.hasNext()) {
                String line = reading.nextLine();
                System.out.println(line);
                list.addDataToList(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= 3; i++) {
            Runnable encodeDataInMyList = () -> {

                while (!list.checkIfAllDataAreEncoded()) {

                    DataAndIndex dataAndIndex = list.takeFirstDecoded();
                    ByteArrayInputStream in = new ByteArrayInputStream(dataAndIndex.getData().getBytes());
                    ByteArrayOutputStream out = new ByteArrayOutputStream(dataAndIndex.getData().getBytes().length);
                    try {
                        encrypt(in, out);
                    } catch (IOException | GeneralSecurityException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    list.saveEncodedData(new DataAndIndex(out.toString(), dataAndIndex.getArrayIndex()));
                    System.out.println(" ");
                    list.print();
                }
            };
            new Thread(encodeDataInMyList).start();
        }

    }

    private static void encrypt(InputStream in, ByteArrayOutputStream out) throws
            IOException, ClassNotFoundException, GeneralSecurityException {
        int mode = Cipher.ENCRYPT_MODE;

        try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream("key.txt"));) {
            Key key = (Key) keyIn.readObject();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, key);
            Util.crypt(in, out, cipher);
        }
    }

}
