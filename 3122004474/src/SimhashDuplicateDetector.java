import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimhashDuplicateDetector {

    // Simhash位数
    private static final int SIMHASH_BITS = 64;

    // 分词器
    private Tokenizer tokenizer;

    public SimhashDuplicateDetector(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    // 计算Simhash值
    private long calculateSimhash(String text) {
        // 初始化特征向量
        int[] features = new int[SIMHASH_BITS];
        // 分词
        String[] tokens = tokenizer.tokenize(text);
        // 计算每个词的hash值，并将其与权重加到特征向量中
        for (String token : tokens) {
            long hash = MurmurHash.hash64(token.getBytes());
            for (int i = 0; i < SIMHASH_BITS; i++) {
                long bitmask = 1L << i;
                if ((hash & bitmask) != 0) {
                    features[i]++;
                } else {
                    features[i]--;
                }
            }
        }
        // 根据特征向量生成Simhash值
        long simhash = 0;
        for (int i = 0; i < SIMHASH_BITS; i++) {
            if (features[i] > 0) {
                simhash |= (1L << i);
            }
        }
        return simhash;
    }

    // 计算Hamming距离
    private int hammingDistance(long hash1, long hash2) {
        long xor = hash1 ^ hash2;
        int distance = 0;
        while (xor != 0) {
            distance++;
            xor &= (xor - 1);
        }
        return distance;
    }

    // 检测文本是否重复
    public double calculateDuplicateRate(String text1, String text2) {
        long hash1 = calculateSimhash(text1);
        long hash2 = calculateSimhash(text2);
        int distance = hammingDistance(hash1, hash2);
        return 1 - (double) distance / SIMHASH_BITS;
    }

    // 示例中文分词器
    private static class Tokenizer {
        public String[] tokenize(String text) {
            // 这里使用空格分词，实际应用中可以使用更复杂的分词器
            return text.split("\\s+");
        }
    }

    // MurmurHash算法，用于计算字符串的hash值
    private static class MurmurHash {
        public static long hash64(final byte[] data) {
            final long m = 0xc6a4a7935bd1e995L;
            final int r = 47;
            long h = 0x8445d61a4e774912L ^ (data.length * m);
            int length = data.length;
            int i = 0;
            while (length >= 8) {
                long k = data[i++];
                k |= ((long) data[i++]) << 8;
                k |= ((long) data[i++]) << 16;
                k |= ((long) data[i++]) << 24;
                k |= ((long) data[i++]) << 32;
                k |= ((long) data[i++]) << 40;
                k |= ((long) data[i++]) << 48;
                k |= ((long) data[i++]) << 56;
                k *= m;
                k ^= k >>> r;
                k *= m;
                h ^= k;
                h *= m;
                length -= 8;
            }
            switch (length) {
                case 7:
                    h ^= (long) (data[i++]) << 48;
                case 6:
                    h ^= (long) (data[i++]) << 40;
                case 5:
                    h ^= (long) (data[i++]) << 32;
                case 4:
                    h ^= (long) (data[i++]) << 24;
                case 3:
                    h ^= (long) (data[i++]) << 16;
                case 2:
                    h ^= (long) (data[i++]) << 8;
                case 1:
                    h ^= (long) (data[i++]);
                    h *= m;
            }
            h ^= h >>> r;
            h *= m;
            h ^= h >>> r;
            return h;
        }
    }

    public static void main(String[] args) {
        // 文件路径
        String file1 = "D:\\\\C_File\\\\word1et\\\\orig.txt";
        String file2 = "D:\\\\C_File\\\\word1et\\\\orig_0.8_del.txt";
        String outputFile = "D:\\\\C_File\\\\word1et\\\\empty.txt";

        // 初始化查重器
        Tokenizer tokenizer = new Tokenizer();
        SimhashDuplicateDetector detector = new SimhashDuplicateDetector(tokenizer);

        // 读取文件内容
        String text1 = readFileContent(file1);
        String text2 = readFileContent(file2);

        // 计算重复率
        double duplicateRate = detector.calculateDuplicateRate(text1, text2);

        // 输出重复率到文件
        writeToFile(outputFile, String.format("重复率: %.4f", duplicateRate));
    }

    // 读取文件内容
    private static String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    // 写入文件
    private static void writeToFile(String filePath, String content) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
