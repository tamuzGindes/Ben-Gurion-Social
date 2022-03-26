CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: Client
Client: bin/ConnectionHandler.o bin/ReadKeyboard.o bin/ReadSocket.o bin/Client.o
	g++ -o bin/Client bin/ConnectionHandler.o bin/ReadKeyboard.o bin/ReadSocket.o bin/Client.o $(LDFLAGS)


bin/ConnectionHandler.o: src/ConnectionHandler.cpp include/ConnectionHandler.h
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/ReadKeyboard.o: src/ReadKeyboard.cpp include/ReadKeyboard.h
	g++ $(CFLAGS) -o bin/ReadKeyboard.o src/ReadKeyboard.cpp

bin/ReadSocket.o: src/ReadSocket.cpp include/ReadSocket.h
	g++ $(CFLAGS) -o bin/ReadSocket.o src/ReadSocket.cpp

bin/Client.o: src/Client.cpp include/ConnectionHandler.h include/ReadSocket.h include/ReadKeyboard.h
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
