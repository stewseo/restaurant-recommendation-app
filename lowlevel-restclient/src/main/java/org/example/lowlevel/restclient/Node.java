package org.example.lowlevel.restclient;


import org.apache.http.*;

import java.util.*;

public class Node {

    private final HttpHost host;

    private final Set<HttpHost> boundHosts;

    private final String name;

    private final String version;

    private final Roles roles;

    private final Map<String, List<String>> attributes;

    public Node(HttpHost host, Set<HttpHost> boundHosts, String name, String version, Roles roles, Map<String, List<String>> attributes) {
        if (host == null) {
            throw new IllegalArgumentException("host cannot be null");
        }
        this.host = host;
        this.boundHosts = boundHosts;
        this.name = name;
        this.version = version;
        this.roles = roles;
        this.attributes = attributes;
    }


    public Node(HttpHost host) {
        this(host, null, null, null, null, null);
    }


    public HttpHost getHost() {
        return host;
    }

    public Set<HttpHost> getBoundHosts() {
        return boundHosts;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }


    public Roles getRoles() {
        return roles;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[host=").append(host);
        if (boundHosts != null) {
            b.append(", bound=").append(boundHosts);
        }
        if (name != null) {
            b.append(", name=").append(name);
        }
        if (version != null) {
            b.append(", version=").append(version);
        }
        if (roles != null) {
            b.append(", roles=").append(roles);
        }
        if (attributes != null) {
            b.append(", attributes=").append(attributes);
        }
        return b.append(']').toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Node other = (Node) obj;
        return host.equals(other.host)
                && Objects.equals(boundHosts, other.boundHosts)
                && Objects.equals(name, other.name)
                && Objects.equals(version, other.version)
                && Objects.equals(roles, other.roles)
                && Objects.equals(attributes, other.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, boundHosts, name, version, roles, attributes);
    }

    /**
     * Role information about an Elasticsearch process.
     */
    public static final class Roles {

        private final Set<String> roles;

        public Roles(final Set<String> roles) {
            this.roles = new TreeSet<>(roles);
        }

        /**
         * Returns whether or not the node <strong>could</strong> be elected master.
         */
        public boolean isMasterEligible() {
            return roles.contains("master");
        }

        /**
         * Returns whether or not the node stores data.
         * @deprecated use {@link #hasDataRole()} or {@link #canContainData()}
         */
        @Deprecated
        public boolean isData() {
            return roles.contains("data");
        }

        /**
         * @return true if node has the "data" role
         */
        public boolean hasDataRole() {
            return roles.contains("data");
        }

        /**
         * @return true if node has the "data_content" role
         */
        public boolean hasDataContentRole() {
            return roles.contains("data_content");
        }

        /**
         * @return true if node has the "data_hot" role
         */
        public boolean hasDataHotRole() {
            return roles.contains("data_hot");
        }

        /**
         * @return true if node has the "data_warm" role
         */
        public boolean hasDataWarmRole() {
            return roles.contains("data_warm");
        }

        /**
         * @return true if node has the "data_cold" role
         */
        public boolean hasDataColdRole() {
            return roles.contains("data_cold");
        }

        /**
         * @return true if node has the "data_frozen" role
         */
        public boolean hasDataFrozenRole() {
            return roles.contains("data_frozen");
        }

        /**
         * @return true if node stores any type of data
         */
        public boolean canContainData() {
            return hasDataRole() || roles.stream().anyMatch(role -> role.startsWith("data_"));
        }

        /**
         * Returns whether or not the node runs ingest pipelines.
         */
        public boolean isIngest() {
            return roles.contains("ingest");
        }

        @Override
        public String toString() {
            return String.join(",", roles);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            Roles other = (Roles) obj;
            return roles.equals(other.roles);
        }

        @Override
        public int hashCode() {
            return roles.hashCode();
        }

    }
}