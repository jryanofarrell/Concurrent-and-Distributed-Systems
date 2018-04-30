package paxos;
import java.io.Serializable;

/**
 * Please fill in the data structure you use to represent the response message for each RMI call.
 * Hint: You may need a boolean variable to indicate ack of acceptors and also you may need proposal number and value.
 * Hint: Make it more generic such that you can use it for each RMI call.
 */
public class Response implements Serializable {
    static final long serialVersionUID=2L;
    
    
    // your data here
    public Object value;
    public int highest_prepare_seen;
    public int highest_accept_seen;


	public boolean ok = false;


	public int highest_done_seq;
    

    // Your constructor and methods here
}
