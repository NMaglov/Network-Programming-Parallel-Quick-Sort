package base.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 4;
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private int readInt(SocketChannel socketChannel) throws IOException {
        byteBuffer.clear();
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        return byteBuffer.getInt();
    }

    public void work() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    try {
                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            if (key.attachment() == null) {
                                int size = readInt(socketChannel);
                                key.attach(new Attachment(size));
                            } else {
                                Attachment attachment = (Attachment) key.attachment();
                                attachment.pushBack(readInt(socketChannel));
                                if (attachment.getArr().length == attachment.getPosition()) {
                                    try (ParallelQuickSort sorting = new ParallelQuickSort(10)) {
                                        int[] sorted = sorting.sort(attachment.getArr());
                                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * sorted.length);
                                        for (int i = 0; i < sorted.length; i++) {
                                            byteBuffer.putInt(sorted[i]);
                                        }
                                        byteBuffer.flip();
                                        socketChannel.write(byteBuffer);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        } else if (key.isAcceptable()) {
                            ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
                            SocketChannel accept = socketChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, null);
                        }

                        keyIterator.remove();
                    } catch (Exception e) {
                        keyIterator.remove();
                        key.cancel();
                        key.channel().close();

                    }
                }

            }

        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }
    }

}
