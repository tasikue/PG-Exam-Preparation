import java.sql.SQLException;
import dao.BankAccountDAO;
import dto.BankAccount;
import input.InputHandler;
import constants.MessageConstants;
import constants.TransactionConstants;

/**
 * ATMクラス
 */
public class Atm {
    /** 現在の操作対象口座 */
    private BankAccount accountInUse;
    /** 入力ハンドラー */
    private InputHandler inputHandler;
    /** データベースアクセスオブジェクト */
    private BankAccountDAO dao;

    public void startAtm(){
        inputHandler = new InputHandler();

        dao = new BankAccountDAO();

        System.out.print(MessageConstants.INFO_START_SYSTEM);

        try{
            authenticateAccount();

            menu();
        }catch(SQLException e){
            System.out.print(MessageConstants.WARNING_SQL_ERROR);
        }

        System.out.print(MessageConstants.INFO_END_SYSTEM);

        inputHandler.close();
    }

    /**
     * 操作対象口座の認証処理
     */
    private void authenticateAccount() throws SQLException{
        final int ACCOUNT_NO_DIGITS = 8;
        final int PASSWORD_DIGITS = 4;

        BankAccount accountToValidate = null;
        boolean isValidAccount = false;

        while(!isValidAccount){
            int accountNo = inputHandler.inputNumber(MessageConstants.INPUT_ACCOUNTNO, ACCOUNT_NO_DIGITS);
            int pin = inputHandler.inputNumber(MessageConstants.INPUT_PIN, PASSWORD_DIGITS);

            // 入力した口座番号に該当す銀行口座を取得する
            accountToValidate = dao.getAccountInfoByNoAndPin(accountNo, pin);

            if(accountToValidate != null){
                System.out.print(MessageConstants.AUTHENTICATION_SUCCESS);
                isValidAccount = true;
            }else{
                System.out.print(MessageConstants.AUTHENTICATION_FAIL);
            }
        }

        accountInUse = accountToValidate;
    }

    private void refreshAccountInfo() throws SQLException{
        accountInUse = dao.getAccountInfoByNo(accountInUse.getAccountNo());
    }

    /**
     * 各種取引を実行するメソッド
     */
    private void menu() throws SQLException{
        boolean isEndOfTransaction = false;

        while(!isEndOfTransaction){
            System.out.print(MessageConstants.TRANSACTION_MENU);
            int transactionInput = inputHandler.inputNumber(MessageConstants.INPUT_TRANSACTION_NUMBER);

            switch(transactionInput){
                case TransactionConstants.DEPOSIT:
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_START);
                    deposit();
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_END);
                    break;
                case TransactionConstants.WITHDRAW:
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_START);
                    withdraw();
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_END);
                    break;
                case TransactionConstants.BALANCE:
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_START);
                    balance();
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_END);
                    break;
                case TransactionConstants.TRANSFER:
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_START);
                    transfer();
                    System.out.print(MessageConstants.TRANSACTION_SEPARATOR_END);
                    break;
                case TransactionConstants.END_TRANSACTION:
                    isEndOfTransaction = true;
                    break;
                default:
                    System.out.print(MessageConstants.WARNING_INVALID_TRANSACTION_NUMBER);
            }
        }
    }

    /**
     * お預入れ
     */
    private void deposit() throws SQLException{
        System.out.print(MessageConstants.TRANSACTION_HEADER_DEPOSIT);
        int amount = inputHandler.inputPositiveNumber(MessageConstants.INPUT_DEPOSIT_AMOUNT);

        dao.deposit(accountInUse.getAccountNo(),amount);

        refreshAccountInfo();

        int balance = accountInUse.getBalance();
        System.out.printf(MessageConstants.OUTPUT_BALANCE_AFTER_TRANSACTION,balance);
    }

    /**
     * お引出し
     */
    private void withdraw() throws SQLException{
        System.out.print(MessageConstants.TRANSACTION_HEADER_WITHDRAW);
        int withdrawAmount = inputHandler.inputPositiveNumber(MessageConstants.INPUT_WITHDRAW_AMOUNT);
        
        boolean hasBalance = dao.hasSuficientBalance(accountInUse.getAccountNo(), withdrawAmount);
        
        if(!hasBalance){
            System.out.print(MessageConstants.WARNING_INSUFFICIENT_BALANCE);
            return;
        }

        dao.withdraw(accountInUse.getAccountNo(), withdrawAmount);
        
        refreshAccountInfo();

        int balance = accountInUse.getBalance();
        System.out.printf(MessageConstants.OUTPUT_BALANCE_AFTER_TRANSACTION,balance);
    }

    /**
     * 残高照会
     */
    private void balance() throws SQLException{
        System.out.print(MessageConstants.TRANSACTION_HEADER_BALANCE);
        refreshAccountInfo();
        int balance = accountInUse.getBalance();
        System.out.printf(MessageConstants.OUTPUT_BALANCE,balance);
    }

    /**
     * お振込み
     */
    private void transfer() throws SQLException{
        System.out.print(MessageConstants.TRANSACTION_HEADER_TRANSFER);
        int accountNo = inputHandler.inputNumber(MessageConstants.INPUT_TRANSFER_DESTINATION, 8);

        BankAccount destinationAccount = dao.getAccountInfoByNo(accountNo);

        if(destinationAccount == null){
            System.out.print(MessageConstants.WARNING_INVALID_TRANSFER_DESTINATION);
            return;
        }

        int transferAmount = inputHandler.inputPositiveNumber(MessageConstants.INPUT_TRANSFER_AMOUNT);

        boolean hasBalance = dao.hasSuficientBalance(accountInUse.getAccountNo(), transferAmount);

        if(!hasBalance){
            System.out.print(MessageConstants.WARNING_INSUFFICIENT_BALANCE);
            return;
        }

        dao.withdraw(accountInUse.getAccountNo(), transferAmount);
        dao.deposit(destinationAccount.getAccountNo(), transferAmount);

        refreshAccountInfo();
        
        int balance = accountInUse.getBalance();

        String receiverName = destinationAccount.getHolderName();
        System.out.printf(MessageConstants.OUTPUT_RECEIVER_NAME, receiverName);
        System.out.printf(MessageConstants.OUTPUT_TRANSFER_AMOUNT, transferAmount);
        System.out.printf(MessageConstants.OUTPUT_BALANCE_AFTER_TRANSACTION, balance);
    }
}
