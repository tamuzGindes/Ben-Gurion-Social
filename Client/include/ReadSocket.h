//
// Created by hadar4@wincs.cs.bgu.ac.il on 28/12/2021.
//

#ifndef BOOST_ECHO_CLIENT_READSOCKET_H
#define BOOST_ECHO_CLIENT_READSOCKET_H
#include <iostream>
#include <mutex>
#include <thread>
#include "../include/ConnectionHandler.h"


class ReadSocket {
private:
    ConnectionHandler* connectionHandler;
    std::mutex* mutex;
    std::map<short,std::string> mapOfOp;

    void close();
    short otherOpCode = -1;
    short opCode = -1;
    char op[2];
    char otherOp[2];
    std::string socketAns;
    std::string decoded = "";
public:
    ReadSocket(ConnectionHandler* conHandler, std::mutex* mutex);
    short bytesToShort(char* bytesArr);
    void run();
    void reset();

};


#endif //BOOST_ECHO_CLIENT_READSOCKET_H
