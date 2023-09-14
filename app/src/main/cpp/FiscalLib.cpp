#include <jni.h>
#include <string>
#include "sqlite3.h"
#include <unistd.h>
#define NUMBER_OF_SALEITEMS 256
#define NUMBER_OF_PAYITEMS 16
typedef struct _UserInfo
{
    int userId;
    char nameSurname[20];
    char password[12];
}UserInfo;
typedef struct _SaleItem
{
    int itemId;
    const char* itemName;
    int itemPrice;
    int vatRate;
}SaleItem;
typedef struct _PayItem
{
    int payType; // 1 : CASH, 2: CREDIT, 3: QR
    int payAmount;
}PayItem;
typedef struct _Receipt
{
    int receiptNo;
    const char * receiptDate;
    SaleItem items[NUMBER_OF_SALEITEMS];
    PayItem payments[NUMBER_OF_PAYITEMS];
}Receipt;


static UserInfo users[10];

extern "C"
JNIEXPORT void JNICALL
Java_com_example_saleapp_LoginActivity_init(JNIEnv *env, jobject thiz, jstring path) {
    sqlite3 *dbHandler;
    char *errmsg = 0;
    int rc=sqlite3_open(env->GetStringUTFChars(path,0), &dbHandler);
    if(rc==SQLITE_OK) {
        sqlite3_exec(dbHandler, "CREATE TABLE IF NOT EXISTS ReceiptSummary(" \
                           "ID INT PRIMARY KEY AUTOINCREMENT," \
                           "ReceiptNo INT,"
                                "ReceiptDate VARCHAR(20),"
                                "ReceiptTotal REAL,"
                                "CreditPayment REAL,"
                                "CashPayment REAL,"
                                "QRPayment REAL", NULL, 0, &errmsg);
        sqlite3_exec(dbHandler, "CREATE TABLE IF NOT EXISTS ReceiptDetails(" \
                           "ID INT PRIMARY KEY AUTOINCREMENT,"\
                           "FOREIGN KEY(ReceiptID) REFERENCES ReceiptSummary(ID),"\
                           "ProductID INT,"\
                           "ProductName VARCHAR(50),"\
                           "Price INT,"\
                           "VatRate INT", NULL, NULL, &errmsg);
    }
    users[0].userId = 1;
    strcpy(users[0].nameSurname, "user1");
    users[1].userId = 2131;
    strcpy(users[1].nameSurname, "user2");
    users[2].userId = 1111;
    strcpy(users[2].nameSurname, "user3");
    users[3].userId = 1211;
    strcpy(users[3].nameSurname, "user4");
    users[4].userId = 5474;
    strcpy(users[4].nameSurname, "user5");
    users[5].userId = 7899;
    strcpy(users[5].nameSurname, "user6");
    users[6].userId = 9864;
    strcpy(users[6].nameSurname, "user7");
    users[7].userId = 2145;
    strcpy(users[7].nameSurname, "user8");
    users[8].userId = 326;
    strcpy(users[8].nameSurname, "ahmetuzun");
    users[9].userId = 4444;
    strcpy(users[9].nameSurname, "user10");
    for (int i = 0; i < 10; i++)
        sprintf(users[i].password,"ABC%d%d",users[i].userId,users[i].userId+1);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_saleapp_LoginActivity_login(JNIEnv *env, jobject thiz, jint id, jstring password) {
    const char * passIn= env->GetStringUTFChars(password, 0);
    for(int i=0;i<10;i++){
        if(users[i].userId==id) {
            if (strcmp(users[i].password, passIn)==0){
                return true;
            }
            else
                return false;
        }
    }
    return false;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_saleapp_SaleActivity_saveReceipt(JNIEnv *env, jobject thiz, jobject r) {
    _Receipt receipt;
    jclass receiptData_class = env->GetObjectClass(r);

    jfieldID fieldId_Of_receiptStartDate=env->GetFieldID(receiptData_class,"receiptStartDate", "Ljava/lang/String;");
    jobject receiptDate=env->GetObjectField(r,fieldId_Of_receiptStartDate);
    if(receiptDate!=NULL)
        receipt.receiptDate=env->GetStringUTFChars((jstring)receiptDate,0);
    jfieldID fieldId_Of_SaleItemIndex=env->GetFieldID(receiptData_class,"SaleItemIndex", "S");
    jshort SaleItemIndex=env->GetShortField(r,fieldId_Of_SaleItemIndex);
    jfieldID fieldId_Of_SaleItem = env->GetFieldID(receiptData_class, "items","[Lcom/example/saleapp/SaleActivity$SaleItem;");
    jobject SaleItems=env->GetObjectField(r,fieldId_Of_SaleItem);
    if(SaleItemIndex>0 && SaleItems!=NULL) {
        jobjectArray SaleItemsArray = static_cast<jobjectArray>(SaleItems);
//        jint length_Of_items = env->GetArrayLength(SaleItemsArray);
//        receipt.items = (_SaleItem *) malloc(sizeof(_SaleItem) * SaleItemIndex);
        for (int i = 0; i < NUMBER_OF_SALEITEMS; i++) {
            jobject SaleItem = env->GetObjectArrayElement(SaleItemsArray, i);
            if(SaleItem!=NULL){
                jclass ClassOfSaleItem = env->GetObjectClass(SaleItem);
                jfieldID fieldId_Of_ItemIdInSaleItem = env->GetFieldID(ClassOfSaleItem, "itemId", "I");
                jint itemId=env->GetIntField(SaleItem, fieldId_Of_ItemIdInSaleItem);
                if(itemId!=NULL)
                    receipt.items[i].itemId = itemId;
                jfieldID fieldId_Of_ItemNameInSaleItem = env->GetFieldID(ClassOfSaleItem, "itemName","Ljava/lang/String;");
                jobject itemName=env->GetObjectField(SaleItem, fieldId_Of_ItemNameInSaleItem);
                if(itemName!=NULL)
                    receipt.items[i].itemName = env->GetStringUTFChars((jstring) itemName,0);
                jfieldID fieldId_Of_ItemPriceInSaleItem = env->GetFieldID(ClassOfSaleItem, "itemPrice","I");
                jint itemPrice=env->GetIntField(SaleItem, fieldId_Of_ItemPriceInSaleItem);
                if(itemPrice!=NULL)
                    receipt.items[i].itemPrice = itemPrice;
                jfieldID fieldId_Of_VatRateInSaleItem = env->GetFieldID(ClassOfSaleItem, "vatRate","I");
                jint vatRate=env->GetIntField(SaleItem, fieldId_Of_VatRateInSaleItem);
                if(vatRate!=NULL)
                    receipt.items[i].vatRate = vatRate;
            }
            else
            {
                receipt.items[i].itemId=NULL;
                receipt.items[i].itemPrice=NULL;
                receipt.items[i].itemName=NULL;
            }
        }
    }
    jfieldID fieldId_Of_PayItem = env->GetFieldID(receiptData_class,"payItems", "[Lcom/example/saleapp/SaleActivity$PayItem;");
    jobject PayItems=env->GetObjectField(r,fieldId_Of_PayItem);
    jfieldID fieldId_Of_PayItemIndex=env->GetFieldID(receiptData_class,"PayItemIndex", "S");
    jshort PayItemIndex=env->GetShortField(r,fieldId_Of_PayItemIndex);
    if(PayItemIndex>0 && PayItems!=NULL) {
        jobjectArray PayItemsArray = static_cast<jobjectArray>(PayItems);
//        jint length_Of_Payments = env->GetArrayLength(PayItemsArray);
//        receipt.payments = (_PayItem *) malloc(sizeof(_PayItem) * PayItemIndex);
        for (int i = 0; i < NUMBER_OF_PAYITEMS; i++) {
            jobject PayItem = env->GetObjectArrayElement(PayItemsArray, i);
            if(PayItem!=NULL) {
                jclass ClassOfPayItem = env->GetObjectClass(PayItem);
                jfieldID fieldId_Of_PayType = env->GetFieldID(ClassOfPayItem, "PayType", "I");
                jint payType=env->GetIntField(PayItem, fieldId_Of_PayType);
                if(payType!=NULL)
                    receipt.payments[i].payType = payType;
                jfieldID fieldId_Of_PayAmount = env->GetFieldID(ClassOfPayItem, "PayAmount", "I");
                jint payAmount=env->GetIntField(PayItem, fieldId_Of_PayAmount);
                if(payAmount!=NULL)
                    receipt.payments[i].payAmount = payAmount;
            }
        }
    }
    if(receipt.items[4].itemId!=NULL)
        return receipt.items[4].itemId;
    return -1;
//    return sizeof(receipt.items)/sizeof(SaleItem);
}