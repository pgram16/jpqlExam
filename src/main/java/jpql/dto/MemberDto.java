package jpql.dto;

public class MemberDto {
    private Long id;
    private String name;

    public MemberDto(String name) {
        this.name = name;
    }

    public MemberDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
