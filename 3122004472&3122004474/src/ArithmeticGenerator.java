
import java.io.*;
import java.util.*;

    public class ArithmeticGenerator {
        // 定义运算符号
        private static final char[] OPERATORS = {'+', '-', '×', '÷'};
        private static final Random RANDOM = new Random();
        private static final Scanner scanner = new Scanner(System.in);

        public static void main(String[] args) {
            // 手动设置题目数量和范围
            int numberOfExercises = getNumberOfExercisesFromUser(); // 从用户输入获取题目数量
            int range = getRangeFromUser(); // 从用户输入获取范围

            // 获取文件夹路径
            String directory = "C:\\Users\\Administrator\\IdeaProjects\\_4ze_timu_shengcheng\\ceshiyongli"; // 修改为你想要存储文件的路径

            // 获取文件名
            String exerciseFileName = "Exercises";
            String answerFileName = "Answers";

            int fileIndex = 1;
            String exerciseFile;
            String answerFile;

            do {
                exerciseFile = directory + File.separator + exerciseFileName + fileIndex + ".txt";
                answerFile = directory + File.separator + answerFileName + fileIndex + ".txt";
                fileIndex++;
            } while (fileExists(exerciseFile) || fileExists(answerFile));

            // 生成题目并写入文件
            generateExercisesAndAnswers(numberOfExercises, range, exerciseFile, answerFile);

            System.out.println("Exercises and answers generated successfully.");
        }

        private static int getNumberOfExercisesFromUser() {
            System.out.println("Enter the number of exercises:");
            return scanner.nextInt();
        }

        private static int getRangeFromUser() {
            System.out.println("Enter the range:");
            return scanner.nextInt();
        }

        private static void generateExercisesAndAnswers(int numberOfExercises, int range, String exerciseFileName, String answerFileName) {
            try (PrintWriter exerciseWriter = new PrintWriter(new FileWriter(exerciseFileName));
                 PrintWriter answerWriter = new PrintWriter(new FileWriter(answerFileName))) {

                for (int i = 1; i <= numberOfExercises; i++) {
                    String expression = generateExpression(range);
                    String answer = calculateAnswer(expression);

                    exerciseWriter.println("四则运算题目" + i + ": " + expression);
                    answerWriter.println("答案" + i + ": " + answer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static String generateExpression(int range) {
            int num1 = RANDOM.nextInt(range) + 1;
            int num2 = RANDOM.nextInt(range) + 1;
            char operator = OPERATORS[RANDOM.nextInt(OPERATORS.length)];
            // 避免产生负数
            if (operator == '-' && num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            // 避免产生除不尽的情况
            if (operator == '÷') {
                num2 = RANDOM.nextInt(Math.min(range, num1 - 1)) + 1;
            }
            return num1 + " " + operator + " " + num2;
        }

        private static String calculateAnswer(String expression) {
            String[] tokens = expression.split(" ");
            int num1 = Integer.parseInt(tokens[0]);
            int num2 = Integer.parseInt(tokens[2]);
            char operator = tokens[1].charAt(0);

            int result = 0;
            switch (operator) {
                case '+':
                    result = num1 + num2;
                    break;
                case '-':
                    result = num1 - num2;
                    break;
                case '×':
                    result = num1 * num2;
                    break;
                case '÷':
                    result = num1 / num2;
                    break;
            }
            return String.valueOf(result);
        }

        private static boolean fileExists(String filePath) {
            File file = new File(filePath);
            return file.exists();
        }
    }
