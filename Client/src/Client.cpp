//
// Created by hadar4@wincs.cs.bgu.ac.il on 28/12/2021.
//

#include "../include/ConnectionHandler.h"
#include "../include/ReadKeyboard.h"
#include "../include/ReadSocket.h"
using namespace std;

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    std::mutex mutex;
    ReadKeyboard readKBoard(&connectionHandler, &mutex);
    ReadSocket readSocket(&connectionHandler, &mutex);
    thread keyboardThread(&ReadKeyboard::run, &readKBoard);
    thread socketThread(&ReadSocket::run, &readSocket);
    keyboardThread.join();
    socketThread.join();
}