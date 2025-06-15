package obj;
public class Employee {
	private Integer id;
    private String employeeId;
    private String employeeName;
    private String role;
    private Integer base_salary;
    private String note;
    private String phone;
    private String image;
	public Integer getId() {
		return id;
	}
	public Employee(Integer id, String employeeId, String employeeName, String role, Integer base_salary, String note,
			String phone, String image) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.role = role;
		this.base_salary = base_salary;
		this.note = note;
		this.phone = phone;
		this.image = image;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRole() {
		return role;
	}
	public Integer getBase_salary() {
		return base_salary;
	}
	public String getNote() {
		return note;
	}
	public String getImage() {
		return image;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public void setBase_salary(Integer base_salary) {
		this.base_salary = base_salary;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Employee(Integer id, String employeeId, String employeeName, String role, Integer base_salary, String note,
			String image) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.role = role;
		this.base_salary = base_salary;
		this.note = note;
		this.image = image;
	}
	public Employee(Integer id, String employeeId, String employeeName, String role, Integer base_salary, String note) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.role = role;
		this.base_salary = base_salary;
		this.note = note;
	}
	@Override
    public String toString() {
        return "Employee{" +
                "name='" + employeeName + '\'' +
                ", role='" + role + '\'' +
                ", base salary=" + base_salary +
                ", description='" + note + '\'' +
                '}';
    }
}
