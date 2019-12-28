package homework2;

import java.util.*;

public class Participant implements Simulatable<String>{
    /**
     * @modifies this, graph
     * @effects Simulates this filter in a system modeled by graph
     */


    private boolean checkRep() {
        return _need.getAmount() >= 0;
    }

    private String _label;
    private Transaction _need;
    private List<Transaction> _storageBuffer = new ArrayList<>();
    private List<Transaction> _trans = new ArrayList<>();

    /**
     * @requires _need.getAmount() >= 0
     * @return a new instance of Participant labeled 'label' with transaction 'need', which is the needed product.
     */
    public Participant(String label, Transaction need) {
        _label = label;
        _need = need;
        assert checkRep();
    }

    /**
     * @modifies _storageBuffer - adding transaction
     */
    private void storeTrans (String productName, int amount){
        assert checkRep();
        for(Transaction t : _storageBuffer){
            if(t.getProduct().equals(productName)){
                t.setAmount(t.getAmount() + amount);
                amount = 0;
                break;
            }
        }
        if(amount > 0){
            _storageBuffer.add(new Transaction(productName, amount));
        }
        assert checkRep();
    }

    /**
     * @modifies this, graph
     * @effects Simulates this participant in a system modeled by graph
     */
    @Override
    public void simulate(BipartiteGraph<String> graph) {
        assert checkRep();
        Iterator<Transaction> transIter = _trans.iterator();
        while(transIter.hasNext()) {
            Transaction t = transIter.next();
            //assert (t.getAmount() > 0);
            Collection<String> childrenNames = graph.listChildren(_label);
            for (String pipeName : childrenNames) {
                Channel child = (Channel)graph.findNode(pipeName).getData();
                if(child.addTrans(t)){//successful transaction
                    transIter.remove();
                    assert checkRep();
                    return;
                }
            }
        }
        assert checkRep();
    }

    /**
     * @modifies this
     * @effects add transaction t to the Participant
     */
    public void addTrans(Transaction t){
        assert checkRep();
        int addedAmount = 0;
        addedAmount = Math.min(t.getAmount(), _need.getAmount());
        if(addedAmount > 0){
            storeTrans(t.getProduct(), addedAmount);
            t.setAmount(t.getAmount() - addedAmount);
            _need.setAmount(_need.getAmount() - addedAmount);
        }
        if(t.getAmount() > 0){
            _trans.add(t);
        }
        assert checkRep();
    }

    /**
     * @return The sum of all Transaction amount of stored products that in the storage buffer.
     */
    public int getStorageAmount(){
        assert checkRep();
        int res = 0;
        for (Transaction t : _storageBuffer) {
            res += t.getAmount();
        }
        assert checkRep();
        return res;
    }

    /**
     * @return The sum of all Transaction amount of waiting to be recycled.
     */
    public int getRecycleAmount(){
        assert checkRep();
        int res = 0;
        for (Transaction t : _trans) {
            res += t.getAmount();
        }
        assert checkRep();
        return res;
    }
}
