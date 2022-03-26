
1) how to run our code:

Open the Server:
open terminal in Server folder where src and pom files are.
if you want to run TPCMain run the commands:
mvn clean
mvn package
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args=7777

if you want to run ReactorMain run the commands:
mvn clean
mvn package
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 4" (4 is the num of threads in the thread pool)

Open clients:
for each client you want to open do the following:
open terminal in Client folder (makefile is in this folder)
run the commands:
make
bin/Client 127.0.0.1 7777

(now you can enter messages to server)



1.2) An example for each message:

REGISTER <Username> <Password> <Birthday (DD-MM-YYYY)>     EXAMPLE: REGISTER itay itay123 20-01-2000
LOGIN <Username> <Password> <Captcha (0/1)>    EXAMPLE: LOGIN itay itay123 0
LOGOUT
FOLLOW <0/1 (Follow/Unfollow)> <UserName>    EXAMPLE: FOLLOW 0 nimrod
POST <PostMsg>     EXAMPLE: POST hello world
PM <Username> <Content>     EXAMPLE: PM nimrod hello nimrod
LOGSTAT
STAT <UserNames_list (seperated by '|')>     EXAMPLE: STAT ido|nimrod|itay
BLOCK <UserName>      EXAMPLE: BLOCK nimrod

*IMPORTANT- if you entered wrong input syntax and exception thrown you should re run the code (even if the program didnt shut down).

2) We store filtered set of words in BidiMessagingProtocolImpl.java class fields in a array of strings called filteredWords




