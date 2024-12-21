/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project1;

/**
 *
 * @author sophi
 */
class Node<T> {
    public T data;
    public Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

public class LinkedList<T> {
    public Node<T> tail;
    public Node<T> head;

    public LinkedList() {
        this.tail = null;
        this.head = null;
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public void remove(int count) {
    if (head == null) return; 

    Node<T> current = head;
    int size = size(); 
    int stopIndex = size - count; 

    if (stopIndex <= 0) {
        head = null;
        tail = null;
        return;
    }
    for (int i = 1; i < stopIndex; i++) {
        current = current.next;
    }
    current.next = null;
    tail = current;
}


    public int size() {
        int size = 0;
        Node<T> current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    public T get(int index) {
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            if (i == index) {
                return current.data;
            }
            current = current.next;
            i++;
        }
        return null;
    }

    public void set(int index, T data) {
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            if (i == index) {
                current.data = data;
                return;
            }
            current = current.next;
            i++;
        }
    }
}
