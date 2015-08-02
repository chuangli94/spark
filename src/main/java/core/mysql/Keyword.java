package core.mysql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="keywords")
public class Keyword {
	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	

	@Column(nullable = false)
	private String name;
	
	@Column
	private int category;
	

	protected Keyword (){}

	
	public Keyword(String name, int category){
		this.name = name;
		this.category = category;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	
	
	public int getCategory(){
		return category;
	}

	public void setCategory(int category){
		this.category = category;
	}
}
