package proCollab.projectManagement.capstoneProject.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long note_id;
    
    private String title;
    
    @Lob
    private String description;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @ManyToOne(fetch = FetchType.EAGER) // Change to EAGER fetching
    @JoinColumn(name = "owner_id")
    private User note_owner;

    public Note() {
    }

    public Note(long note_id, String title, String description, LocalDate date, User note_owner) {
        this.note_id = note_id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.note_owner = note_owner;
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getNote_owner() {
        return note_owner;
    }

    public void setNote_owner(User note_owner) {
        this.note_owner = note_owner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (note_id ^ (note_id >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((note_owner == null) ? 0 : note_owner.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Note other = (Note) obj;
        if (note_id != other.note_id)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (note_owner == null) {
            if (other.note_owner != null)
                return false;
        } else if (!note_owner.equals(other.note_owner))
            return false;
        return true;
    }

}
