/*
 * Copyright (C) 2020-present, Chenai Nakam(chenai.nakam@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 14/01/2020
 */
public class List<T> {
    private Node<T> head;

    public List(T... os) {
        Node<T> curr = head;
        for (T o : os) {
            if (curr == null) {
                curr = new Node<>();
                head = curr;
            } else {
                curr = curr.next = new Node<>();
            }
            curr.data = o;
        }
    }

    public void reverse() {
        head = reverse(null, head);
        /*
        Node<T> prev = null, curr = head, next;
        while (curr != null) {
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;
        */
    }

    private Node<T> reverse(Node<T> prev, Node<T> curr) {
        if (curr == null) {
            return prev;
        } else {
            Node<T> next = curr.next;
            curr.next = prev;
            return reverse(curr, next);
        }
    }

    public void println() {
        System.out.println("print list--->");
        Node<T> curr = head;
        while (curr != null) {
            System.out.println(curr.data);
            curr = curr.next;
        }
        System.out.println("Done.");
    }

    static class Node<T> {
        T data;
        Node<T> next;

        @Override
        public String toString() {
            return String.valueOf(data);
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new List<>(1, 2, 3, 4, 5);
        list.println();

        list.reverse();
        list.println();

        list.reverse();
        list.println();
    }
}
