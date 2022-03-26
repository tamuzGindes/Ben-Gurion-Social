//
// Created by hadar4@wincs.cs.bgu.ac.il on 28/12/2021.
//

#include "../include/ReadKeyboard.h"

using std::string;
ReadKeyboard::ReadKeyboard(ConnectionHandler *conHandler, std::mutex *mutex) : connectionHandler(conHandler),
                                                                               mutex(mutex) {
    mapOfOp = {{"REGISTER",     1},
                                            {"LOGIN",        2},
                                            {"LOGOUT",       3},
                                            {"FOLLOW",       4},
                                            {"POST",         5},
                                            {"PM",           6},
                                            {"LOGSTAT",      7},
                                            {"STAT",         8},
                                            {"BLOCK", 12}};
}

void ReadKeyboard::run() {
    while (!connectionHandler->getStopKeyboardRead()) {
        if (connectionHandler->getLoggedInStatus()) {
            const short buffsize = 1024;
            char buff[buffsize];
            std::cin.getline(buff, buffsize);
            string line(buff);
            std::vector<string> *param = new std::vector<std::string>();
            std::string delim = " ";
            auto start = 0U;
            auto end = line.find(delim);
            while (end != std::string::npos) {
                param->push_back(line.substr(start, end - start));
                start = end + 1;
                end = line.find(delim, start);
            }
            param->push_back(line.substr(start, line.size()));
            int opCode = -1;
            char *opcodeBytes = new char[2];
            std::vector<char> *bytes = new std::vector<char>();
            string action = line.substr(0, line.find_first_of(' '));
            string toSend = "";
            if (mapOfOp.find(action) != mapOfOp.end()) {
                toSend.push_back(0);
                toSend.push_back(mapOfOp.at(action));
                if (action.compare("REGISTER") == 0) {
                    toSend.append(param->at(1));
                    toSend.push_back('\0');
                    toSend.append(param->at(2));
                    toSend.push_back('\0');
                    toSend.append(param->at(3));
                    toSend.push_back(';');
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("LOGIN") == 0) {
                    toSend.append(param->at(1));
                    toSend.push_back('\0');
                    toSend.append(param->at(2));
                    toSend.push_back('\0');
                    if (param->at(3) == "1") {
                        toSend.append(std::to_string('\1'));
                    } else {
                        toSend.append(std::to_string('\0'));
                    }
                    toSend.push_back(';');
                    connectionHandler->sendLine(toSend);
                    // TODO: if login successful captcha = 1
                } else if (action.compare("LOGOUT") == 0) {
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("FOLLOW") == 0) {
                    if (param->at(1) == "1") {
                        toSend.append(std::to_string('\1'));
                    } else {
                        toSend.append(std::to_string('\0'));
                    }
                    toSend.append(param->at(2));
                    toSend.push_back('\0');
                    toSend.push_back(';');
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("POST") == 0) {
                    opCode = 5;
                    shortToBytes(5, opcodeBytes);
                    int numOfParams = param->size();
                    for (int i = 1; i < numOfParams; i++) {
                        toSend.append(param->at(i));
                        toSend.append(" ");
                    }
                    toSend.push_back(';');
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("PM") == 0) {
                    toSend.append(param->at(1));
                    toSend.push_back('\0');
                    int numOfParams = param->size();
                    for (int i = 1; i < numOfParams; i++) { //fixed
                        toSend.append(param->at(i));
                        toSend.append(" ");
                    }
                    toSend.push_back(';');
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("LOGSTAT") == 0) {
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("STAT") == 0) {
                    toSend.append(param->at(1));
                    toSend.push_back('\0');
                    connectionHandler->sendLine(toSend);
                } else if (action.compare("BLOCK") == 0) {
                    toSend.append(param->at(1));
                    toSend.push_back(';');
                    connectionHandler->sendLine(toSend);
                }
            }
            else{
                std::cout<<"No Such Command"<<std::endl;
            };
        }
        else{
            exit(EXIT_SUCCESS);
        }
    }
}

short ReadKeyboard::bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
void ReadKeyboard::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}



