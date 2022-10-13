package com.example.ll_restclient;



import java.util.*;

public interface NodeSelector {
    /**
     * Select the {@link Node}s to which to send requests. This is called with
     * a mutable {@link Iterable} of {@linkplain Node}s in the order that the
     * rest client would prefer to use them and implementers should remove
     * nodes from the that should not receive the request. Implementers may
     * iterate the nodes as many times as they need.
     * <p>
     * This may be called twice per request: first for "living" nodes that
     * have not been blacklisted by previous errors. If the selector removes
     * all nodes from the list or if there aren't any living nodes then the
     * {@link RestClient} will call this method with a list of "dead" nodes.
     * <p>
     * Implementers should not rely on the ordering of the nodes.
     */
    void select(Iterable<Node> nodes);
    /*
     * We were fairly careful with our choice of Iterable here. The caller has
     * a List but reordering the list is likely to break round robin. Luckily
     * Iterable doesn't allow any reordering.
     */

    /**
     * Selector that matches any node.
     */
    NodeSelector ANY = new NodeSelector() {
        @Override
        public void select(Iterable<Node> nodes) {
            // Intentionally does nothing
        }

        @Override
        public String toString() {
            return "ANY";
        }
    };

    NodeSelector SKIP_DEDICATED_MASTERS = new NodeSelector() {
        @Override
        public void select(Iterable<Node> nodes) {
            for (Iterator<Node> itr = nodes.iterator(); itr.hasNext();) {
                Node node = itr.next();
                if (node.getRoles() == null) continue;
                if (node.getRoles().isMasterEligible()
                        && false == node.getRoles().canContainData()
                        && false == node.getRoles().isIngest()) {
                    itr.remove();
                }
            }
        }

        @Override
        public String toString() {
            return "SKIP_DEDICATED_MASTERS";
        }
    };
}
