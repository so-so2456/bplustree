package org.dfpl.lecture.database.assignment2.assignment2_20011750;

import java.util.*;

@SuppressWarnings("unused")
public class MyBPlusTree implements NavigableSet<Integer> {

    /********** 2023-05-29 수정 *************
    *    remove에 changeKeytoSuccessor 추가  *
    ****************************************/

    // Data Abstraction은 예시일 뿐 자유롭게 B+ Tree의 범주 안에서 어느정도 수정가능
    private MyBPlusTreeNode root; // 루트 노드
    private LinkedList<MyBPlusTreeNode> leafList; // 리프 노드 연결 리스트
    private int m; // m-way b+tree

    public MyBPlusTree(int m) {
        this.m = m;
        this.leafList = new LinkedList<>();
    }

    // 디버깅 할때 쓴 preorder 순회
    public void printBPlusTree() {
        rPrintBPlusTree(this.root);
    }
    public void rPrintBPlusTree(MyBPlusTreeNode node) {
        if (node == null) return;
        System.out.println(node.keyList);
        for (var child: node.children) {
            rPrintBPlusTree(child);
        }
    }

    /**
     * 과제 Assignment4를 위한 메소드:
     * <p>
     * key로 검색하면 root부터 시작하여, key를 포함할 수 있는 leaf node를 찾고 key가 실제로 존재하면 해당 Node를
     * 반환하고, 그렇지 않다면 null을 반환한다. 중간과정을 System.out.println(String) 으로 출력해야 함. 6 way
     * B+ tree에서 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21 이 순서대로
     * add되었다고 했을 때,
     * <p>
     * 예: getNode(11)을 수행하였을 때
     * > larger than or equal to 10
     * > less than 13
     * > 11 found
     * 위의 3 문장을
     * 콘솔에 출력하고 11을 포함한 MyBPlusTreeNode를 반환함
     * <p>
     * 예: getNode(22)를 수행하였을 때
     * > larger than or equal to 10
     * > larger than or equal to 19
     * > 22 not found
     * 위의 3
     * 문장을 콘솔에 출력하고 null을 반환함.
     *
     * @param key
     * @return
     */
	/*
	B+Tree 탐색 알고리즘
		1. 비어있으면 바로 null 리턴
		2. key가 들어있는 리프노드 탐색
			a. leaf 이면 반환
			b. keyList 중 key 보다 큰 element를 만날때까지 인덱스 증가
			c. 아니면 child로 내려가 a 반복
		3. 탐색한 리프노드에 key가 들어있으면 노드 반환, 아니면 null 반환
	 */
    public MyBPlusTreeNode getNode(Integer key) {
        // 1. 비어있으면 바로 null 리턴
        if (isEmpty()) {
            System.out.printf("%d not found\n", key);
            return null;
        }
        // 2. key가 들어있는 리프노드 탐색
        var target = rSearchNode(this.root, key, true);
        // 3. 탐색한 리프노드에 key가 들어있으면 노드 반환, 아니면 null 반환
        // 성능 향상을 위한 BS 알고리즘 사용
        int index = Collections.binarySearch(target.keyList.subList(0, target.numOfKey), key);
        if (index < 0) {
            System.out.printf("%d not found\n", key);
            return null;
        } else {
            System.out.printf("%d found\n", key);
            return target;
        }
    }

    public MyBPlusTreeNode rSearchNode(MyBPlusTreeNode node, Integer key, boolean verbose) {
        // 2.a. leaf 이면 반환
        if (node.height == 0) {
            return node;
        }
        // 2.b. keyList 중 key 보다 크거나 같은 element를 만날때까지 인덱스 증가
        int i = 0;
        while (node.keyList.get(i) != null && key >= node.keyList.get(i)) {
            i++;
        }
        // 다른 메서드에서도 탐색을 사용하기 위해 조건문으로 처리
        if (verbose) {
            if (i == 0) System.out.printf("less than %d\n", node.keyList.get(i));
            else System.out.printf("larger than or equal to %d\n", node.keyList.get(i - 1));
        }
        // 2.c. child로 내려가 a 반복
        return rSearchNode(node.children.get(i), key, verbose);
    }

    /**
     * 과제 Assignment4를 위한 메소드:
     * <p>
     * inorder traversal을 수행하여, 값을 오름차순으로 출력한다.
     * 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22 이 순서대로
     * add되었다고 했을 때,
     * 1
     * 2
     * 3
     * 4
     * 5
     * 6
     * 7
     * 8
     * 9
     * 10
     * 11
     * 12
     * 13
     * 14
     * 15
     * 16
     * 17
     * 18
     * 19
     * 20
     * 21
     * 22
     * 위와 같이 출력되어야 함.
     */
    // b+tree 루트부터  순회를 하여 현재 노드가 리프노드이면 키 리스트 출력
    public void inorderTraverse() {
        rInorderTraverse(this.root);
    }
    public void rInorderTraverse(MyBPlusTreeNode node) {
        if (node == null) return;
        if (this.leafList.contains(node)) {
            for (var key: node.keyList) {
                if (key != null) System.out.println(key);
                else break;
            }
        } else {
            for (var child: node.children) {
                rInorderTraverse(child);
            }
        }
    }

    // b+tree의 크기 반환
    @Override
    public int size() {
        if (isEmpty()) return 0;
        int length = 0;
        for (var node : this.leafList) {
            length += node.numOfKey;
        }
        return length;
    }

    // B+tree가 비었는지 확인
    @Override
    public boolean isEmpty() {
        return this.leafList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    /*
    B+tree 삽입 알고리즘
    1. 비었으면 새로운 노드를 만들어 리프노드에 추가
    2. 아니면, 삽입할 리프노드 찾고 삽입 후 정렬
        a. 노드가 꽉 차지 않았을땐 패스
        b. 노드가 꽉 찾을땐 분할
     */
    @Override
    public boolean add(Integer e) {
        // 1. 비었으면 새로운 노드를 만들어 리프노드에 추가
        if (isEmpty()) {
            var keyList = new ArrayList<Integer>(Collections.nCopies(this.m, null));
            keyList.set(0, e);
            var newNode = new MyBPlusTreeNode(this.m, keyList, 0);
            this.leafList.add(newNode);
            this.root = newNode;
            return true;
        }
        // 2. 아니면, 삽입할 리프 노드 찾고 삽입 후 정렬
        var node = rSearchNode(this.root, e, false);
        node.keyList.set(node.numOfKey, e);
        node.numOfKey++;
        Collections.sort(node.keyList.subList(0, node.numOfKey));
        // 2.a. 노드가 꽉 차지 않았을땐 패스
        if (!isOverflow(node)) {
            return true;
        }
        // 2.b. 노드가 꽉 찾을땐 분할
        splitLeafNode(node);
        return true;
    }

    // 노드에 키가 꽉 찼는지 확인
    public boolean isOverflow(MyBPlusTreeNode node) {
        return node.numOfKey >= this.m;
    }

    /*
    B+tree 리프노드 분할 알고리즘
        * 기존 노드는 왼쪽 자식, 새로운 노드는 오른쪽 자식으로 설정함
        1. 오버플로우된 키 리스트를 분할하여 리턴(왼쪽, 오른쪽 자식 키 리스트 준비)
        2. 루트인지 확인
            a. 루트이면, 새로운 노드를 만들어 새로운 키 리스트의 첫번째 원소로 새로운 루트를 만든 다음, 기존 노드를 왼쪽 자식으로 연결
            b. 아니면, 새로운 키 리스트의 첫번째 원소를 부모 키 리스트에 삽입한 뒤 정렬
        3. 오른쪽 자식에 대해 새로운 노드를 만들고, 왼쪽 자식 바로 옆에 오른쪽 자식을 삽입한 뒤, sibling 설정
            a. left <=> right <=> (nullable 노드)
        4. 부모가 overflow 되었는지 확인하고, overflow 되었다면 분할
     */
    public void splitLeafNode(MyBPlusTreeNode leftNode) {
        int splitIndex = getLeafSplitIndex();
        // 1. 오버플로우된 키 리스트를 분할하여 새로운 키 리스트 리턴
        // 왼쪽, 오른쪽 자식 키 리스트 준비
        var halfKeyList = splitKeyList(splitIndex, leftNode.keyList);
        leftNode.numOfKey = leftNode.getKeyListSize(leftNode.keyList);
        // 2. 루트인지 확인
        if (leftNode.parent == null) {
            // 2.a. 루트이면, 새로운 노드를 만들어 새로운 키 리스트의 첫번째 원소로 새로운 루트를 만든 다음, 기존 노드를 왼쪽 자식으로 연결
            var parentKeyList = new ArrayList<Integer>(Collections.nCopies(this.m, null));
            parentKeyList.set(0, halfKeyList.getFirst());
            var parent = new MyBPlusTreeNode(this.m, parentKeyList, leftNode.height + 1);
            parent.children.set(parent.numOfChildren, leftNode);
            parent.numOfChildren++;
            leftNode.parent = parent;
            this.root = leftNode.parent;
        }
        else {
            // 2.b. 아니면, 새로운 키 리스트의 첫번째 원소를 부모 키 리스트에 삽입한 뒤 정렬
            leftNode.parent.keyList.set(leftNode.parent.numOfKey, halfKeyList.getFirst());
            leftNode.parent.numOfKey++;
            Collections.sort(leftNode.parent.keyList.subList(0, leftNode.parent.numOfKey));
        }
        // 3. 오른쪽 자식에 대해 새로운 노드를 만들고
        var rightNode = new MyBPlusTreeNode(this.m, halfKeyList, leftNode.height);
        // 왼쪽 자식 바로 옆에 오른쪽 자식을 삽입한 뒤
        int i = leftNode.parent.children.indexOf(leftNode);
        leftNode.parent.rightShiftChildrenFromIndex(i + 1);
        leftNode.parent.children.set(i + 1, rightNode);
        leftNode.parent.numOfChildren++;
        rightNode.parent = leftNode.parent;
        i = this.leafList.indexOf(leftNode);
        this.leafList.add(i + 1, rightNode);
        // sibling 설정
        rightNode.rightSibling = leftNode.rightSibling;
        if (rightNode.rightSibling != null) {
            rightNode.rightSibling.leftSibling = rightNode;
        }
        leftNode.rightSibling = rightNode;
        rightNode.leftSibling = leftNode;
        // 4. 부모가 overflow 되었는지 확인하고, overflow 되었다면 분할
        var node = leftNode.parent;
        while (node != null) {
            if (isOverflow(node)) splitInternalNode(node);
            else break;
            node = node.parent;
        }
    }

    // 리프 노드일 때 분할되는 지점
    public int getLeafSplitIndex() {
        return (int) Math.ceil((this.m + 1) / 2.0) - 1;
    }

    // 내부 노드일 때 분할되는 지점
    public int getInternalSplitIndex() {
        if (this.m == 3) return (int) Math.ceil((this.m + 1) / 2.0) - 1;
        else return (int) Math.ceil((this.m - 1) / 2.0) - 1;
    }

    // 키 리스트가 overflow 되었을 때 분할 지점 기준으로 나눔
    public ArrayList<Integer> splitKeyList(int splitIndex, ArrayList<Integer> keyList) {
        var halfKeyList = new ArrayList<Integer>(Collections.nCopies(this.m, null));
        for (int i = splitIndex; i < keyList.size(); i++) {
            halfKeyList.set(i - splitIndex, keyList.get(i));
            keyList.set(i, null);
        }
        return halfKeyList;
    }

    /*
    B+tree 내부노드 분할 알고리즘
        * 기존 노드는 왼쪽 자식, 새로운 노드는 오른쪽 자식으로 설정함
        1. 오버플로우된 키 + 자식 리스트 리스트를 분할하여 리턴(왼쪽, 오른쪽 자식 키 + 자식 리스트 준비)
        2. 오른쪽 자식에 대해 새로운 노드를 만들고, sibling 설정
            * left <=> right <=> (nullable 노드)
        3. 부모 갱신
            a. 루트이면, 분할 지점의 원소로 새로운 루트를 만들고, 왼쪽, 오른쪽 자식을 삽입
            b. 아니면, 분할 지점의 원소를 부모 키 리스트에 삽입한 뒤 정렬, 그리고 왼쪽, 오른쪽 자식을 삽입
     */
    public void splitInternalNode(MyBPlusTreeNode leftNode) {
        // 1. 오버플로우된 키 + 자식 리스트 리스트를 분할하여 새로운 키 + 자식 리스트 리턴
        // 왼쪽, 오른쪽 자식 키 + 자식 리스트 준비
        var parent = leftNode.parent;
        int splitIndex = getInternalSplitIndex();
        var parentKey = leftNode.keyList.get(splitIndex);
        var halfKeyList = splitKeyList(splitIndex + 1, leftNode.keyList);
        leftNode.keyList.set(leftNode.getKeyListSize(leftNode.keyList) - 1, null);
        var halfChildren = splitChildren(splitIndex + 1, leftNode.children);
        leftNode.numOfKey = leftNode.getKeyListSize(leftNode.keyList);
        leftNode.numOfChildren = leftNode.getChildrenSize(leftNode.children);
        // 2. 오른쪽 자식에 대해 새로운 노드를 만들고
        var rightNode = new MyBPlusTreeNode(this.m, halfKeyList, halfChildren, leftNode.height);
        for (var child : halfChildren) {
            if (child != null) child.parent = rightNode;
        }
        rightNode.parent = parent;
        // sibling 설정
        rightNode.rightSibling = leftNode.rightSibling;
        if (rightNode.rightSibling != null) {
            rightNode.rightSibling.leftSibling = rightNode;
        }
        leftNode.rightSibling = rightNode;
        rightNode.leftSibling = leftNode;
        // 3. 부모 갱신
        if (parent == null) {
            // 3.a. 루트이면, 분할 지점의 원소로 새로운 루트를 만들고, 왼쪽, 오른쪽 자식을 삽입
            var parentKeyList = new ArrayList<Integer>(Collections.nCopies(this.m, null));
            var parentChildren = new ArrayList<MyBPlusTreeNode>(Collections.nCopies(this.m + 1, null));
            parentKeyList.set(0, parentKey);
            parentChildren.set(0, leftNode);
            parentChildren.set(1, rightNode);
            var newRoot = new MyBPlusTreeNode(this.m, parentKeyList, parentChildren, leftNode.height + 1);
            leftNode.parent = newRoot;
            rightNode.parent = newRoot;
            this.root = newRoot;
        }
        else {
            // 3.b. 아니면, 분할 지점의 원소를 부모 키 리스트에 삽입한 뒤 정렬, 그리고 왼쪽, 오른쪽 자식을 삽입
            parent.keyList.set(parent.numOfKey, parentKey);
            parent.numOfKey++;
            Collections.sort(parent.keyList.subList(0, parent.numOfKey));
            int i = parent.children.indexOf(leftNode);
            parent.rightShiftChildrenFromIndex(i + 1);
            parent.children.set(i + 1, rightNode);
            parent.numOfChildren++;
            rightNode.parent = parent;
        }
    }

    // overflow 되었을 때 분할 지점 기준으로 자식 리스트를 나눔
    public ArrayList<MyBPlusTreeNode> splitChildren(int splitIndex, ArrayList<MyBPlusTreeNode> children) {
        var halfChildren = new ArrayList<MyBPlusTreeNode>(Collections.nCopies(this.m + 1, null));
        for (int i = splitIndex; i < halfChildren.size(); i++) {
            halfChildren.set(i - splitIndex, children.get(i));
            children.set(i, null);
        }
        return halfChildren;
    }

    /*
        B+tree 삭제 알고리즘
            1. 비어있으면 바로 리턴
            2. 아니면, 탐색하여 키를 찾고
                a. 없으면 바로 리턴
                b. 아니면, 키 삭제
            3. 노드가 underflow 되면 조정하고, 아니면 비었는지 확인 후 리턴
            4. 내부 노드에 삭제한 키를 계승자의 키로 바꿔줌
     */
    @Override
    public boolean remove(Object o) {
        Integer key = (Integer) o;
        // 1. 비어있으면 바로 리턴
        if (isEmpty()) return false;
        // 2. 아니면, 탐색하여 키를 찾고
        var target = rSearchNode(this.root, key, false);
        int index = Collections.binarySearch(target.keyList.subList(0, target.numOfKey), key);
        // 2.a. 없으면 바로 리턴
        if (index < 0) return false;
        // 2.b. 아니면, 키 삭제
        else {
            target.keyList.set(index, null);
            target.leftShiftKeyListFromIndex(index);
            target.numOfKey--;
        }
        // 3. 노드가 underflow 되면 조정하고, 아니면 비었는지 확인 후 리턴
        if (this.size() != 0 && isUnderflow(target)) {
            handleLeafNodeUnderflow(target);
        } else if (target.parent == null && target.numOfKey == 0) {
            this.root = null;
            this.leafList.clear();
        }
        // 4. 내부 노드에 삭제한 키를 계승자의 키로 바꿔줌
        changeKeytoSuccessor(key);
        return true;
    }
    // 삭제한 키로 순회하여 같은 값을 가진 노드를 기준으로 계승자를 찾아 값을 바꾸어 줌
    public void changeKeytoSuccessor(Integer key) {
        rChangeKeytoSuccessor(this.root, key);
    }
    public void rChangeKeytoSuccessor(MyBPlusTreeNode node, Integer key) {
        if (node == null) return;
        int i = 0;
        while (node.keyList.get(i) != null && key >= node.keyList.get(i)) {
            i++;
        }
        if (i != 0 && Objects.equals(node.keyList.get(i - 1), key)) {
            int successor = getSuccessor(node.children.get(i));
            node.keyList.set(i - 1, successor);
        }
        rChangeKeytoSuccessor(node.children.get(i), key);
    }
    // 해당 노드에서 계승자를 찾음
    public int getSuccessor(MyBPlusTreeNode node) {
        while (node.numOfChildren > 0) {
            node = node.children.getFirst();
        }
        return node.keyList.getFirst();
    }
    // 노드가 underflow 되었는지 확인
    public boolean isUnderflow(MyBPlusTreeNode node) {
        return node.numOfKey < node.minNumOfKey;
    }

    /*
        B+tree 리프노드 조정 알고리즘
            1. (왼쪽/오른쪽) 형제 노드에게 빌릴 수 있는지 확인
                a. 빌릴 수 있다면 현재 노드에 빌린 키를 추가
                b. 형제 노드에 빌려준 키 삭제
                c. 부모 노드 키 재설정
            2. (왼쪽/오른쪽) 형제 노드와 합칠 수 있는지 확인
                a. 현재 노드 모든 키를 형제 노드에게 이동
                b. 부모 노드에서 현재 노드의 연결 삭제 및 재설정
                c. 형제 노드의 형제 재설정
                d. 현재 노드 삭제
                e. 부모가 underflow 되었는지 확인 후 조정
     */
    public void handleLeafNodeUnderflow(MyBPlusTreeNode target) {
        var parent = target.parent;
        MyBPlusTreeNode sibling;
        // 1. 왼쪽 형제 노드에게 빌릴 수 있는지 확인
        if (target.leftSibling != null && target.leftSibling.parent == parent && target.leftSibling.isLendable()) {
            // 1.a. 빌릴 수 있다면 현재 노드에 빌린 키를 추가
            sibling = target.leftSibling;
            var borrowedKey = sibling.keyList.get(sibling.numOfKey - 1);
            target.keyList.set(target.numOfKey, borrowedKey);
            target.numOfKey++;
            Collections.sort(target.keyList.subList(0, target.numOfKey));
            // 1.b. 형제 노드에 빌려준 키 삭제
            sibling.keyList.set(sibling.numOfKey - 1, null);
            sibling.numOfKey--;
            // 1.c. 부모 노드 키 재설정
            int i = parent.children.indexOf(target);
            parent.keyList.set(i - 1, target.keyList.getFirst());
        }
        // 1. 오른쪽 형제 노드에게 빌릴 수 있는지 확인
        else if (target.rightSibling != null && target.rightSibling.parent == parent && target.rightSibling.isLendable()) {
            // 1.a. 빌릴 수 있다면 현재 노드에 빌린 키를 추가
            sibling = target.rightSibling;
            var borrowedKey = sibling.keyList.getFirst();
            target.keyList.set(target.numOfKey, borrowedKey);
            target.numOfKey++;
            Collections.sort(target.keyList.subList(0, target.numOfKey));
            // 1.b. 형제 노드에 빌려준 키 삭제
            sibling.keyList.set(0, null);
            sibling.leftShiftKeyListFromIndex(0);
            sibling.numOfKey--;
            // 1.c. 부모 노드 키 재설정
            int i = parent.children.indexOf(target);
            if (i != 0) parent.keyList.set(i - 1, target.keyList.getFirst());
            parent.keyList.set(i, sibling.keyList.getFirst());
        }
        // 2. 왼쪽 형제 노드와 합칠 수 있는지 확인
        else if (target.leftSibling != null && target.leftSibling.parent == parent && target.leftSibling.isMergeable()) {
            // 2.a. 현재 노드 모든 키를 형제 노드에게 이동
            sibling = target.leftSibling;
            for (int i = 0; i < target.numOfKey; i++) {
                sibling.keyList.set(sibling.numOfKey, target.keyList.get(i));
                sibling.numOfKey++;
            }
            // 2.b. 부모 노드에서 현재 노드의 연결 삭제 및 재설정
            int i = parent.children.indexOf(target);
            parent.keyList.set(i - 1, null);
            parent.leftShiftKeyListFromIndex(i - 1);
            parent.numOfKey--;
            parent.children.set(i, null);
            parent.leftShiftChildrenFromIndex(i);
            parent.numOfChildren--;
            // 2.c. 형제 노드의 형제 재설정
            sibling.rightSibling = target.rightSibling;
            if (sibling.rightSibling != null) {
                sibling.rightSibling.leftSibling = sibling;
            }
            // 2.d. 현재 노드 삭제
            this.leafList.remove(target);
            // 2.e. 부모가 underflow 되었는지 확인 후 조정
            if (isUnderflow(parent)) {
                handleInternalNodeUnderflow(parent);
            }
        }
        // 2. 오른쪽 형제 노드와 합칠 수 있는지 확인
        else if (target.rightSibling != null && target.rightSibling.parent == parent && target.rightSibling.isMergeable()) {
            // 2.a. 현재 노드 모든 키를 형제 노드에게 이동
            sibling = target.rightSibling;
            for (int i = 0; i < target.numOfKey; i++) {
                sibling.rightShiftKeyListFromIndex(0);
                sibling.numOfKey++;
            }
            for (int i = 0; i < target.numOfKey; i++) {
                sibling.keyList.set(i, target.keyList.get(i));
            }
            // 2.b. 부모 노드에서 현재 노드의 연결 삭제 및 재설정
            int i = parent.children.indexOf(target);
            parent.keyList.set(i, null);
            parent.leftShiftKeyListFromIndex(i);
            parent.numOfKey--;
            parent.children.set(i, null);
            parent.leftShiftChildrenFromIndex(i);
            parent.numOfChildren--;
            // 2.c. 형제 노드의 형제 재설정
            sibling.leftSibling = target.leftSibling;
            if (sibling.leftSibling != null) {
                sibling.leftSibling.rightSibling = sibling;
            }
            // 2.d. 현재 노드 삭제
            this.leafList.remove(target);
            // 2.e. 부모가 underflow 되었는지 확인 후 조정
            if (isUnderflow(parent)) {
                handleInternalNodeUnderflow(parent);
            }
        }
    }
    /*
        B+tree 내부노드 조정 알고리즘
            1. 루트 노드인지 검사
                a. 키가 존재하면 패스
                b. 비어있으면 자식에게 루트를 물려줌
            2. (왼쪽/오른쪽) 형제 노드에게 빌릴 수 있는지 확인
                a. 빌릴 수 있다면 키와 자식을 빌림
                b. 현재 노드는 부모의 키와 형제의 빌려준 자식을 가짐
                c. 부모 노드는 형제의 빌려준 키를 가짐
                d. 형제 노드에 빌려준 키와 자식 삭제
            3. (왼쪽/오른쪽) 형제 노드와 합칠 수 있는지 확인
                a. 부모 노드 하나의 키를 형제 노드로 이동
                b. 현재 노드 모든 키, 자식을 형제 노드로 이동
                c. 부모 노드에서 현재 노드 삭제 및 재설정
                d. 형제 노드의 형제 재설정
            4. 부모가 underflow 되었는지 확인 후 조정
            
     */
    public void handleInternalNodeUnderflow(MyBPlusTreeNode target) {
        var parent = target.parent;
        MyBPlusTreeNode sibling;
        // 1. 루트 노드인지 검사
        if (target == this.root) {
            // 1.a. 키가 존재하면 패스
            if (target.numOfKey > 0) return;
            // 1.b. 비어있으면 자식에게 루트를 물려줌

        }
        if (target == this.root && target.numOfKey == 0) {
            for (int i = 0; i < this.root.numOfChildren; i++) {
                if (target.children.get(i) != null) {
                    this.root = target.children.get(i);
                    this.root.parent = null;
                    target.children.set(i, null);
                    target.numOfChildren--;
                    break;
                }
            }
        }
        // 2. 왼쪽 형제 노드에게 빌릴 수 있는지 확인
        else if (target.leftSibling != null && target.leftSibling.parent == parent && target.leftSibling.isLendable()) {
            // 2.a. 빌릴 수 있다면 키와 자식을 빌림
            sibling = target.leftSibling;
            var borrowedKey = sibling.keyList.get(sibling.numOfKey - 1);
            var borrowedChild = sibling.children.get(sibling.numOfChildren - 1);
            // 2.b. 현재 노드는 부모의 키와 형제의 빌려준 자식을 가짐
            int index = parent.children.indexOf(target);
            target.rightShiftKeyListFromIndex(0);
            target.keyList.set(0, parent.keyList.get(index - 1));
            target.numOfKey++;
            target.rightShiftChildrenFromIndex(0);
            target.children.set(0, borrowedChild);
            target.numOfChildren++;
            // 2.c. 부모 노드는 형제의 빌려준 키를 가짐
            parent.keyList.set(index - 1, borrowedKey);
            // 2.d. 형제 노드에 빌려준 키와 자식 삭제
            sibling.keyList.set(sibling.numOfKey - 1, null);
            sibling.numOfKey--;
            sibling.children.set(sibling.numOfChildren - 1, null);
            sibling.numOfChildren--;
            borrowedChild.parent = target;
        }
        // 2. 오른쪽 형제 노드에게 빌릴 수 있는지 확인
        else if (target.rightSibling != null && target.rightSibling.parent == parent && target.rightSibling.isLendable()) {
            // 2.a. 빌릴 수 있다면 키와 자식을 빌림
            sibling = target.rightSibling;
            var borrowedKey = sibling.keyList.getFirst();
            var borrowedChild = sibling.children.getFirst();
            // 2.b. 현재 노드는 부모의 키와 형제의 빌려준 자식을 가짐
            int index = parent.children.indexOf(target);
            target.keyList.set(target.numOfKey, parent.keyList.get(index));
            target.numOfKey++;
            target.children.set(target.numOfChildren, borrowedChild);
            target.numOfChildren++;
            // 2.c. 부모 노드는 형제의 빌려준 키를 가짐
            parent.keyList.set(index, borrowedKey);
            // 2.d. 형제 노드에 빌려준 키와 자식 삭제
            sibling.keyList.set(0, null);
            sibling.leftShiftKeyListFromIndex(0);
            sibling.numOfKey--;
            sibling.children.set(0, null);
            sibling.leftShiftChildrenFromIndex(0);
            sibling.numOfChildren--;
            borrowedChild.parent = target;
        }
        // 3. 왼쪽 형제 노드와 합칠 수 있는지 확인
        else if (target.leftSibling != null && target.leftSibling.parent == parent && target.leftSibling.isMergeable()) {
            // 3.a. 부모 노드 하나의 키를 형제 노드로 이동
            sibling = target.leftSibling;
            int index = parent.children.indexOf(target);
            sibling.keyList.set(sibling.numOfKey, parent.keyList.get(index - 1));
            sibling.numOfKey++;
            parent.keyList.set(index - 1, null);
            parent.leftShiftKeyListFromIndex(index - 1);
            parent.numOfKey--;
            // 3.b. 현재 노드의 모든 키 + 자식을 형제 노드로 이동
            for (int i = 0; i < target.numOfKey; i++) {
                sibling.keyList.set(sibling.numOfKey, target.keyList.get(i));
                sibling.numOfKey++;
            }
            for (int i = 0; i < target.numOfChildren; i++) {
                sibling.children.set(sibling.numOfChildren, target.children.get(i));
                target.children.get(i).parent = sibling;
                sibling.numOfChildren++;
            }
            // 3.c. 부모 노드에서 현재 노드 삭제 및 재설정
            parent.children.set(index, null);
            parent.leftShiftChildrenFromIndex(index);
            parent.numOfChildren--;
            // 3.d. 형제 노드의 형제 재설정
            sibling.rightSibling = target.rightSibling;
            if (target.rightSibling != null) {
                target.rightSibling.leftSibling = target.leftSibling;
            }
        }
        // 3. 오른쪽 형제 노드와 합칠 수 있는지 확인
        else if (target.rightSibling != null && target.rightSibling.parent == parent && target.rightSibling.isMergeable()) {
            // 3.a. 부모 노드 하나의 키를 형제 노드로 이동
            sibling = target.rightSibling;
            int index = parent.children.indexOf(target);
            sibling.rightShiftKeyListFromIndex(0);
            sibling.keyList.set(0, parent.keyList.get(index));
            sibling.numOfKey++;
            parent.keyList.set(index, null);
            parent.leftShiftKeyListFromIndex(index);
            parent.numOfKey--;
            // 3.b. 현재 노드의 모든 키 + 자식을 형제 노드로 이동
            for (int i = 0; i < target.numOfKey; i++) {
                sibling.rightShiftKeyListFromIndex(0);
                sibling.numOfKey++;
            }
            for (int i = 0; i < target.numOfKey; i++) {
                sibling.keyList.set(i, target.keyList.get(i));
            }
            for (int i = 0; i < target.numOfChildren; i++) {
                sibling.rightShiftChildrenFromIndex(0);
                sibling.numOfChildren++;
            }
            for (int i = 0; i < target.numOfChildren; i++) {
                sibling.children.set(i, target.children.get(i));
                target.children.get(i).parent = sibling;
            }
            // 3.c. 부모 노드에서 현재 노드 삭제 및 재설정
            parent.children.set(index, null);
            parent.leftShiftChildrenFromIndex(index);
            parent.numOfChildren--;
            // 3.d. 형제 노드의 형제 재설정
            sibling.leftSibling = target.leftSibling;
            if (target.leftSibling != null) {
                target.leftSibling.rightSibling = target.rightSibling;
            }
        }
        // 4. 부모가 underflow 되었는지 확인 후 조정
        if (parent != null && isUnderflow(parent)) {
            handleInternalNodeUnderflow(parent);
        }
    }

    /*
        B+tree iterator 클래스 구현
            1. hasNext : 다음 원소가 있는지 확인
            2. next : 다음 원소로 이동
     */
    public class BPlustreeIterator implements Iterator<Integer> {
        private int currentIndex; // 현재 노드 원소의 인덱스
        private int currentNodeIndex; // 현재 노드의 인덱스
        private final LinkedList<MyBPlusTreeNode> list; // 이터레이터

        public BPlustreeIterator(LinkedList<MyBPlusTreeNode> list) {
            this.list = list;
            this.currentIndex = 0;
            this.currentNodeIndex = 0;
        }

        @Override
        public boolean hasNext() {
            // 현재 노드의 다음 원소가 있는지 확인
            if (this.currentIndex < this.list.get(this.currentNodeIndex).numOfKey) {
                return true;
            }
            // 다음 노드가 있는지 확인
            else return this.currentNodeIndex < this.list.size() - 1;
        }

        @Override
        public Integer next() {
            // 다음 원소가 없으면 에러
            if (!hasNext()) throw new NoSuchElementException();
            // 현재 노드 원소가 더 없다면 다음 노드로 이동
            if (this.currentIndex >= this.list.get(this.currentNodeIndex).numOfKey) {
                this.currentNodeIndex++;
                this.currentIndex = 0;
            }
            // 다음 원소 반환
            return this.list.get(this.currentNodeIndex).keyList.get(this.currentIndex++);
        }
    }

    @Override
    public Iterator<Integer> iterator() {
        return new BPlustreeIterator(this.leafList);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    @Override
    public Integer lower(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer floor(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer ceiling(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer higher(Integer e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer pollFirst() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer pollLast() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public NavigableSet<Integer> descendingSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Integer> descendingIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement,
                                        boolean toInclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedSet<Integer> headSet(Integer toElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SortedSet<Integer> tailSet(Integer fromElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer first() {
        return null;
    }

    @Override
    public Integer last() {
        return null;
    }

}
