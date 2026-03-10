package uk.ac.rhul.cs2810.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_id")
    private Long tableId;

    @Enumerated(EnumType.STRING)
    private AlertType type;

    private boolean resolved;

    public Alert() {}

    public Long getId() { return id; }

    public Long getTableId() { return tableId; }

    public void setTableId(Long tableId) { this.tableId = tableId; }

    public AlertType getType() { return type; }

    public void setType(AlertType type) { this.type = type; }

    public boolean isResolved() { return resolved; }

    public void setResolved(boolean resolved) { this.resolved = resolved; }
}