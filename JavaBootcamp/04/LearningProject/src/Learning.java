import java.util.Scanner;

/**
 * 学習用クラス
 */
public class Learning {
    private Scanner scanner;

    /**
     * コンストラクタ
     */
    public Learning(){
        scanner = new Scanner(System.in);
    }

    /**
     * メイン処理
     */
    public void start(){
        // 演算対象の入力
        int firstNumber = inputNumber(MessageConstants.INPUT_FIRST_NUMBER);
        int secondNumber = inputNumber(MessageConstants.INPUT_SECOND_NUMBER);
        // 演算対象の出力
        System.out.printf(MessageConstants.OUTPUT_NUMBERS_TO_CALCULATE, firstNumber, secondNumber);
        // 演算子の入力
        System.out.print(MessageConstants.INPUT_OPERATOR);
        String operator = scanner.nextLine();

        // 演算結果を格納する変数
        int calculationResult = 0;

        switch(operator){
            case "+":
                calculationResult = firstNumber + secondNumber;
                break;
            case "-":
                calculationResult = firstNumber - secondNumber;
                break;
            case "*":
                calculationResult = firstNumber * secondNumber;
                break;
            default:
                operator = "?";
        }

        // 式と演算結果の出力
        System.out.printf("%d %s %d = %d", firstNumber, operator, secondNumber, calculationResult);

        scanner.close();
    }

    /**
     * 数値を一つ入力するメソッド
     * @param inputMessage 入力を要求するメッセ―ジ
     * @return 正しく入力できた数値
     */
    private int inputNumber(String inputMessage){
        boolean isValidInput = false;
        int inputNumber = 0;

        while(!isValidInput){
            System.out.print(inputMessage);
            if(scanner.hasNextInt()){
                inputNumber = scanner.nextInt();
                // nextIntメソッドで読み込まれなかった改行コードを回収
                scanner.nextLine();
                isValidInput = true;
            }else{
                // 数字以外の入力値を回収
                scanner.nextLine();
            }

            if(!isValidInput){
                System.out.println(MessageConstants.WARNING_NOT_INTEGER);
            }
        }
        return inputNumber;
    }
}