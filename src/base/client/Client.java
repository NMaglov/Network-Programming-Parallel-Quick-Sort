package base.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";

    /**
     * Creates ByteBuffer from String.
     *
     * @param input String from which integers will be extracted.
     * @return Returns ByteBuffer containing elements of the input.
     */
    public ByteBuffer stringToByteBuffer(String input) {
        int[] arr = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * (1 + arr.length));
        buffer.clear();
        buffer.putInt(arr.length);
        for (int x : arr) {
            buffer.putInt(x);
        }
//        buffer.flip();
        return buffer;
    }

    public void work() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            socketChannel.configureBlocking(false);

            String input = scanner.nextLine();
//            int[] arr = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
//            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * (1 + arr.length));
//            buffer.clear();
//            buffer.putInt(arr.length);
//            for (int x : arr) {
//                buffer.putInt(x);
//            }
//            buffer.flip();
            ByteBuffer buffer = stringToByteBuffer(input);
            buffer.flip();
            socketChannel.write(buffer);

            int len = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray().length;
            ByteBuffer sorted = ByteBuffer.allocateDirect(4 * len);
            sorted.clear();
            int readBytes = 0;
            while (readBytes < 4 * len) {
                readBytes += socketChannel.read(sorted);
            }
            sorted.flip();
            while (sorted.remaining() > 0) {
                System.out.print(sorted.getInt() + " ");
            }
            System.out.println();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
