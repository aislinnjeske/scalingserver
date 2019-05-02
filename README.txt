~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        HW-2 README
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


cs455.scaling.client 

Client.java             Command line arguments taken: Server hostname, server port number, message rate.
                        Starts a client that connects to the server. Receives messages from the server and verifies the hashes.


SenderThread.java       Sends random messages to the server at the provided message rate. For each message sent, the hash is calculated and                       
                        saved in Client.java.
                        
                        
ClientStatistics.java   Prints the number of messages sent and received by the client in the last 20 seconds. Prints every 20 seconds from creation of      
                        client.
                        

cs455.scaling.server

Server.java             Command line arguments taken: Port number, Thread pool size, Batch size, Batch time.
                        Starts the server that clients will connect to. Receives messages from clients and gives them to the thread pool to be processed.
                        
                        
ThreadPoolManager.java  Creates the threads for the thread pool. Gives tasks to the thread pool if the number of unprocessed tasks is equal to batch size               
                        or batch time has elapsed.
                        
                        
WorkerThread.java       Completes tasks received from the ThreadPoolManager; either connecting a client or reading a message.


ServerStatistics.java   Prints the server throughput, number of active clients, mean per-client throughput, and standard deviation of per-client 
                        throughput from data collected in the last 20 seconds. Prints every 20 seconds from the creation of the server.
                        
                        
cs455.scaling.util


Task.java               Interface that represents a task to be completed by the thread pool


AddToBatch.java		Task to be completed by a thread from the thread pool that adds a task to be batched


Register.java           Task to be completed by a thread from the thread pool that connects a new client      


ProcessData.java        Task to be completed by a thread from the thread pool that reads data sent from a client and sends the hash of the data to the client


Batch.java		Task to be completed by a thread from the thread pool that contains a a list of tasks taht were batched


Hash.java               Calculates SHA-1 hashes



To run server: java cs455.scaling.server.Server port_number thread_pool_size batch_size batch_time

To run client: java cs455.scaling.client.Client server_hostname server_port_number message_rate
