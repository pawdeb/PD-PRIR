# PDlab5.py
#!/usr/bin/env python
# -*- coding: utf-8 -*-
import pika
import threading
import time

# wspólne
connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='logs', exchange_type='fanout')

nick = input('[*] Wpisz swój nick: ')

# receiver
print('[*] Oczekiwanie na wiadomości :)')

def callback(ch, method, properties, body):
    #print("%r" % body.decode())
    print(body.decode())

def task1():

    result = channel.queue_declare(queue='', exclusive=True)
    queue_name = result.method.queue

    channel.queue_bind(exchange='logs', queue=queue_name)

    channel.basic_consume(
        queue=queue_name, on_message_callback=callback, auto_ack=True)

    channel.start_consuming()

#emiter
def task2():

    while(1):
        message = input()
        mess = nick+': '+message
        channel.basic_publish(exchange='logs', routing_key='', body=mess)
    
    connection.close()


t1 = threading.Thread(target=task1)
t2 = threading.Thread(target=task2)

t1.start()
t2.start()

t1.join()
t2.join()
