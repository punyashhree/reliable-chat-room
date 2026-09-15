#Reliable-chat-room-with-Ordered-Messaging

This project is implemented to use DSA concepts of queues. It holds the messages sent at the time of user being offline and proceeds to send them sequentially in a queue based manner once internet connection is back.

#Team-members
Punyashree G, Ritvik Singh, Samikcha Thapa

Built in a 4 hour college hackathon, as part of an assignment


#technologies-used

Java – Backend logic and DSA implementation

HTML, CSS, JavaScript – Frontend user interface

Queue (FIFO) – Core data structure

Git – Version control

VS Code – Development environment


QueueChatProject/

├── backend/

│   └── messagequeue.java

├── frontend/

│   └── index.html

└── README.md


#Core Concepts Implemented

Queue (FIFO) for message ordering

LinkedList as Queue implementation in Java

Sequential message IDs

Offline message queuing

Message synchronization on reconnect


#Message Ordering Logic

Each message is assigned a unique increasing ID

Messages are stored in a queue

FIFO ensures messages are delivered in the same order they were sent.


#Offline Messaging Handling

1.User goes offline

2.Messages are added to the queue

3.Messages remain undelivered

4.On reconnect, queued messages are delivered in order


#Conclusion

This project demonstrates the effective use of the Queue data structure to build a reliable chat system with guaranteed message ordering and offline synchronization.
