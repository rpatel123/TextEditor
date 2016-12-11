package editor;

import javafx.scene.text.Text;

public class LinkedListDeque {

	public class Node {
		public Node prev;
		public Text item;
		public Node next;

		public Node(Node p, Text i, Node n) {
			prev = p;
			item = i;
			next = n;
		}
        public double getTextWidth() {
            if (item.getText() == " " || item.getText() == "\r") {
                return 0;
            }
            if (next != null && next.item.getText() != " " && next.item.getText() != "\r") {
                return item.getLayoutBounds().getWidth() + next.getTextWidth();
            } else {
                return item.getLayoutBounds().getWidth();
            }
        }
	}

	public Node sentinel;
    public Node cursor;
	public int size;
    public static int lineNum = 0;
    public double textWidth;

	public LinkedListDeque() {
		size = 0;
		sentinel = new Node(null, new Text(0,0,""), null);
		cursor = sentinel;
        Editor.lines.add(lineNum, sentinel.next);
        lineNum += 1;
	}


	public void addText(Text i) {
        if (cursor.next == null) {
            cursor.next = new Node(cursor, i, null);
            cursor = cursor.next;
        } else {
            cursor.next.prev = new Node(cursor, i, cursor.next);
            cursor.next = cursor.next.prev;
            cursor = cursor.next;
        }
        size += 1;
    }


	public boolean isEmpty() {
		if (sentinel.next == sentinel) {
			return true;
		}
		else {
			return false;
		}
	}


	public int size() {
		return size;
	}



	public Text removeText() {
        Text oldText = cursor.item;
        if (cursor.next == null) {
            cursor.prev.next = null;
            cursor = cursor.prev;
        } else {
            cursor.prev.next = cursor.next;
            cursor.next.prev = cursor.prev;
            cursor = cursor.prev;
        }
        size -= 1;
        return oldText;
	}

    public Node removeTextNode() {
        Node oldNode = cursor;
        if (cursor.next == null) {
            cursor.prev.next = null;
            cursor = cursor.prev;
        } else {
            cursor.prev.next = cursor.next;
            cursor.next.prev = cursor.prev;
            cursor = cursor.prev;
        }
        size -= 1;
        return oldNode;
    }


	public Text get(int index) {
		Node newSentinel = sentinel;
		int count = 0;
		if (index > size - 1) {
			return null;
		}
		while (count <= index) {
			newSentinel = newSentinel.next;
			count += 1;
		}
		return newSentinel.item;
	}

    public void moveRight() {
        if (cursor.next != null) {
            cursor = cursor.next;
        }
    }

    public void moveLeft() {
        cursor = cursor.prev;
    }

    public boolean textWrap() {
        if (cursor.item.getX() + cursor.item.getLayoutBounds().getWidth() > Editor.windowWidthEditor - 5) {
            return true;
        } else {
            return false;
        }
    }

    public void addNewLine() {
        Editor.lines.add(lineNum, cursor);
        System.out.println(Editor.lines.get(lineNum).item);
        System.out.println(lineNum);
        lineNum += 1;
    }

    public void moveUp() {
        Node cursorUp = cursor;
        double cursorXPos = cursor.item.getLayoutBounds().getWidth() + cursor.item.getX();

        int cursorUpLine = lineNum - 1;
        Node checker = Editor.lines.get(cursorUpLine);



    }

    public void moveDown() {
        Node cursorDown = cursor;
        double cursorPos = cursor.item.getLayoutBounds().getWidth() + cursor.item.getX();

    }

}