//
// Created by hadar4@wincs.cs.bgu.ac.il on 28/12/2021.
//

#ifndef BOOST_ECHO_CLIENT_READKEYBOARD_H
#define BOOST_ECHO_CLIENT_READKEYBOARD_H
#include <mutex>
#include "ConnectionHandler.h"

class ReadKeyboard {
private:
    ConnectionHandler* connectionHandler;
    std::mutex* mutex;
    std::map<std::string , short> mapOfOp;
public:
    ReadKeyboard(ConnectionHandler* conHandler, std::mutex* mutex);
    void shortToBytes(short num, char* bytesArr);
    short bytesToShort(char* bytesArr);
    void run();
};


#endif //BOOST_ECHO_CLIENT_READKEYBOARD_H
