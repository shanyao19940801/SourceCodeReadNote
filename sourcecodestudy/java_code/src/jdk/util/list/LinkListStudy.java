package jdk.util.list;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Created by user on 2018/4/19.
 */
public class LinkListStudy {

    public static void main(String[] args) {
        ArrayList list = new ArrayList<>();
        list.add("");
        list.set(1,"1") ;

    }

    @Test
    public void testLinkedList() {
        LinkedList list = new LinkedList();
        list.get(1);
        list.getFirst();
        list.getLast();
        list.set(1,"");
        list.add("");
        list.add(1,"");;
        list.spliterator();
    }

    @Test
    public void testVector() {
        Vector vector = new Vector();
    }


}
