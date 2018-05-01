package paxos;
import java.io.Serializable;

/**
 * Please fill in the data structure you use to represent the request message for each RMI call.
 * Hint: You may need the sequence number for each paxos instance and also you may need proposal number and value.
 * Hint: Make it more generic such that you can use it for each RMI call.
 * Hint: Easier to make each variable public
 */
public class Request implements Serializable {
    static final long serialVersionUID=1L;
    public int seq;
    public int prop_num;
    public Object value;
    // Your data here


    // Your constructor and methods here
    Request(int seq, int prop_num, Object value){
    	this.seq = seq;
    	this.prop_num = prop_num;
    	this.value = value; 
    }
}
