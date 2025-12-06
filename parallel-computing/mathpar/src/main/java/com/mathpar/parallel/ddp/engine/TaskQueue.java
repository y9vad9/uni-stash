
package com.mathpar.parallel.ddp.engine;
import java.util.ArrayList;

class ListElem{
    AbstractTask ptr;
    int prev,next;
}
public class TaskQueue {
    ArrayList<ListElem> list;
    ArrayList<Integer> stFree;
    int first,last,size;

    public TaskQueue() {
        first=last=-1;
        size=0;
        list=new ArrayList<ListElem>();
        stFree=new ArrayList<Integer>();
    }

    private int Add(AbstractTask ob){
        size++;
        ListElem cur;
        int ind;
        if (stFree.size()>0){
            ind=stFree.remove(stFree.size()-1);
            cur=list.get(ind);
        }
        else {
            cur=new ListElem();
            list.add(cur);
            ind=list.size()-1;
        }
        cur.ptr=ob;
        cur.next=-1;
        if (last!=-1){
            list.get(last).next=ind;
            cur.prev=last;
        }
        else {
            first=ind;
            cur.prev=-1;
        }
        last=ind;
        return ind;
    }
    private void RemoveByIdent(int ident){
        size--;
        int prevPtr=list.get(ident).prev;
        int nextPtr=list.get(ident).next;
        stFree.add(ident);
        if (prevPtr==-1 && nextPtr==-1){
            first=last=-1;
            return;
        }
        if (prevPtr==-1){
            first=nextPtr;
            list.get(nextPtr).prev=-1;
            return;
        }
        if (nextPtr==-1){
            last=prevPtr;
            list.get(prevPtr).next=-1;
            return;
        }
        list.get(prevPtr).next=nextPtr;
        list.get(nextPtr).prev=prevPtr;
    }
    public synchronized boolean TryReturnTaskFromQueue(AbstractTask t){
        if (t.IsTaskSendet())
            return false;
        RemoveByIdent(t.GetIdent());
        return true;
    }
    public synchronized void PushTaskInQueue(AbstractTask t){
        t.SetIdent(Add(t));
    }

    public synchronized ArrayList<AbstractTask> RemoveTasksForSending(int N){
        int cnt=java.lang.Math.min(N, size);
        return GetNFirstElems(cnt);
    }

    private ArrayList<AbstractTask> GetNFirstElems(int N){
        ArrayList<AbstractTask> res=new ArrayList<AbstractTask>();
        int cur=first;
        for (int i=0; i<N; i++){
            list.get(cur).ptr.SetTaskSendet();
            res.add(list.get(cur).ptr);
            int next=list.get(cur).next;
            stFree.add(cur);
            first=next;
            if (next==-1){
                last=-1;
            }
            else {
                list.get(next).prev=-1;
            }
            size--;
            cur=next;
        }
        return res;
    }
    
}
