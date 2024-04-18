/**
 * メッセージの定数クラス
 */
public class MessageConstants {
    public static final String INPUT_FIRST_NUMBER = "1つ目の数値を入力:";
    public static final String INPUT_SECOND_NUMBER = "2つ目の数値を入力:";
    public static final String INPUT_OPERATOR = "次の3つのどれかの演算子を入力してください（+,-,*）:";

    public static final String OUTPUT_NUMBERS_TO_CALCULATE = "演算対象の数字は %d と %d です\n";
    public static final String OUTPUT_CALCULATION_RESULT = "%d %s %d = %d\n";

    public static final String WARNING_NOT_INTEGER = "整数の数値で入力してください";

    /**
     * インスタンス化防止のためのプライベートコンストラクタ
     */
    private MessageConstants(){}
}
