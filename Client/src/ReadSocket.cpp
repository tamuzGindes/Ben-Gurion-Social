//
// Created by hadar4@wincs.cs.bgu.ac.il on 28/12/2021.
//

#include "../include/ReadSocket.h"
#include "../include/ReadKeyboard.h"

using std::string;
ReadSocket::ReadSocket(ConnectionHandler *conHandler, std::mutex *mutex) : connectionHandler(conHandler),
                                                                           mutex(mutex) {
    mapOfOp = {{1,"REGISTER"},
               {2,"LOGIN"},
               {3,"LOGOUT"},
               {4,"FOLLOW"},
               {5,"POST"},
               {6,"PM"},
               {7,"LOGSTAT"},
               {8,"STAT"},
               {9,"NOTIFICATION"},{10,"ACK"},{11,"ERROR"}};

}

void ReadSocket::run() {

    while (!connectionHandler->getStopSocketRead()) {
        if (connectionHandler->dataIsAvailableInSocket()) {
            connectionHandler->getLine(socketAns);
            if (opCode == -1) {
                op[0] = socketAns.at(0);
                op[1] = socketAns.at(1);
                opCode = bytesToShort(op);
                decoded.append(mapOfOp.at(opCode));
                decoded.append(" ");
                if (opCode == 9) {
                    if(socketAns.at(2) == '\0')
                        decoded.append("PM ");
                    else
                        decoded.append("Public ");
                    socketAns = socketAns.substr(3);
                    decoded.append(socketAns.substr(0,socketAns.find_first_of(('\0'))));
                    decoded.append(" ");
                    socketAns = socketAns.substr(socketAns.find_first_of('\0')+1);
                    decoded.append(socketAns.substr(0,socketAns.find_first_of('\0')));
                    decoded.append(" ");
                } else if (opCode == 10) {
                    if (otherOpCode == -1) {
                        otherOp[0] = socketAns.at(2);
                        otherOp[1] = socketAns.at(3);
                        otherOpCode = bytesToShort(otherOp);
                        decoded.append(std::to_string(otherOpCode));
                        decoded.append(" ");
                    }
                    socketAns = socketAns.substr(4,socketAns.size()-1);
                    socketAns = socketAns.substr(0,socketAns.find_first_of(';'));
                    if(otherOpCode == 4){
                        decoded.append(socketAns);
                    }
                    else if((otherOpCode == 8) || (otherOpCode == 7)){
                        char num[2];
                        int sizeOfSocketAns = socketAns.size()-1;
                        for(int i = 0 ; i < sizeOfSocketAns; i=i+2){
                            num[0] = socketAns.at(i);
                            num[1] = socketAns.at(i+1);
                            short n = bytesToShort(num);
                            decoded.append(std::to_string(n));
                            decoded.append(" ");
                        }
                    }
                } else if (opCode == 11) {
                    if (otherOpCode == -1) {
                        otherOp[0] = socketAns.at(2);
                        otherOp[1] = socketAns.at(3);
                        otherOpCode = bytesToShort(otherOp);
                        decoded.append(std::to_string(otherOpCode));
                        decoded.append(" ");
                    }
                }
            }
            std::cout<<decoded<<std::endl;
            // "ACK 3" means logout so we should stop
            if (decoded.find("ACK 3") != std::string::npos) {
                close();
                exit(EXIT_SUCCESS);
            }
            reset();
        }
    }
}


void ReadSocket::close() {
    connectionHandler->setStopSocketRead(true);
    connectionHandler->setStopKeyboardRead(true);
    connectionHandler->setLoggedInStatus(false);
    connectionHandler->close();

}
void ReadSocket::reset(){
     socketAns = "";
     decoded = "";
     otherOpCode = -1;
     opCode = -1;
     op[2];
     otherOp[2];
}

short ReadSocket::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}
