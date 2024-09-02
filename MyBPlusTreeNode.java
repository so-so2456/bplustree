package org.dfpl.lecture.database.assignment2.assignment2_20011750;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unused")
public class MyBPlusTreeNode {
	// Data Abstraction은 예시일 뿐 자유롭게 B+ Tree의 범주 안에서 어느정도 수정가능
	public int maxNumOfChildren; // 노드가 가질 수 있는 최대 자식 수
	public int minNumOfChildren; // 노드가 가져야 하는 최소 자식 수
	public int numOfChildren; // 노드가 현재 가지고 있는 자식 수
	public int maxNumOfKey; // 노드가 가질 수 있는 최대 키 수
	public int minNumOfKey; // 노드가 가져야 하는 최소 키 수
	public int numOfKey; // 노드가 현재 가지고 있는 키 수
	public int height; // 노드의 현재 높이(leaf node height = 0)
	public MyBPlusTreeNode parent; // 노드의 부모
	public MyBPlusTreeNode leftSibling; // 노드의 왼쪽 형제 노드
	public MyBPlusTreeNode rightSibling; // 노드의 오른쪽 형제 노드
	public ArrayList<Integer> keyList; // 키를 담고 있는 리스트
	public ArrayList<MyBPlusTreeNode> children; // 자식을 담고 있는 리스트

	// 생성자 1: 키 리스트, 높이가 주어졌을 때 인스턴스 생성
	public MyBPlusTreeNode(int m, ArrayList<Integer> keyList, int height) {
		this.maxNumOfChildren = m;
		this.minNumOfChildren = (int)Math.ceil(m / 2.0);
		this.numOfChildren = 0;
		this.maxNumOfKey = m - 1;
		this.minNumOfKey = (int)(Math.ceil(m / 2.0) - 1);
		this.numOfKey = getKeyListSize(keyList);
		this.keyList = keyList;
		this.children = new ArrayList<MyBPlusTreeNode>(Collections.nCopies(m + 1, null));
		this.height = height;
	}
	// 생성자 2: 키 리스트, 자식 리스트, 높이가 주어졌을 때 인스턴스 생성
	public MyBPlusTreeNode(int m, ArrayList<Integer> keyList, ArrayList<MyBPlusTreeNode> children, int height) {
		this.maxNumOfChildren = m;
		this.minNumOfChildren = (int)Math.ceil(m / 2.0);
		this.numOfChildren = getChildrenSize(children);
		this.maxNumOfKey = m - 1;
		this.minNumOfKey = (int)(Math.ceil(m / 2.0) - 1);
		this.numOfKey = getKeyListSize(keyList);
		this.keyList = keyList;
		this.children = children;
		this.height = height;
	}
	// 현재 자식 리스트의 크기
	public int getChildrenSize(ArrayList<MyBPlusTreeNode> children) {
		int i = 0;
		while (i < children.size() && children.get(i) != null) i++;
		return i;
	}
	// 현재 키 리스트의 크기
	public int getKeyListSize(ArrayList<Integer> keyList) {
		int i = 0;
		while (i < keyList.size() && keyList.get(i) != null) i++;
		return i;
	}
	// 키 리스트에서 인덱스 기준 오른쪽으로 한칸 씩 밈
	public void rightShiftKeyListFromIndex(int index) {
		for (int i = this.numOfKey; i > index; i--) {
			this.keyList.set(i, this.keyList.get(i - 1));
		}
		this.keyList.set(index, null);
	}
	// 자식 리스트에서 인덱스 기준 오른쪽으로 한칸 씩 밈
	public void rightShiftChildrenFromIndex(int index) {
		for (int i = this.numOfChildren; i > index; i--) {
			this.children.set(i, this.children.get(i - 1));
		}
		this.children.set(index, null);
	}
	// 키 리스트에서 인덱스 기준 왼쪽으로 한칸 씩 밈
	public void leftShiftKeyListFromIndex(int index) {
		for (int i = index; i < this.numOfKey - 1; i++) {
			this.keyList.set(i, this.keyList.get(i + 1));
		}
		this.keyList.set(this.numOfKey - 1, null);
	}
	// 자식 리스트에서 인덱스 기준 왼쪽으로 한칸 씩 밈
	public void leftShiftChildrenFromIndex(int index) {
		for (int i = index; i < this.numOfChildren - 1; i++) {
			this.children.set(i, this.children.get(i + 1));
		}
		this.children.set(this.numOfChildren - 1, null);
	}
	// 다른 노드에게 키를 빌려줄 수 있는지 확인
	public boolean isLendable() {
		return this.numOfKey > this.minNumOfKey;
	}
	// 다른 노드와 합쳐질 수 있는지 확인
	public boolean isMergeable() {
		return this.numOfKey == this.minNumOfKey;
	}
}
