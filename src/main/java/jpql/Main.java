package jpql;

import jpql.dto.MemberDto;
import jpql.entity.Address;
import jpql.entity.Member;
import jpql.entity.Team;

import javax.persistence.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();


        tx.begin();

        try {

//            createTypedQueryExam(em);
//            getResult(em);
            projection(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();

    }

    /**
     * createQuery()
     */
    public static void createTypedQueryExam(EntityManager em) throws Exception {
        Team team = em.find(Team.class, 7L);

        Member member = new Member();
        member.setName("BBB");
        member.setAge(20);
        member.getInTeam(team);
        em.persist(member);

        // 반환타입이 명확한 결과 조회 = TypedQuery
        TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);

        // 반환타입이 불명확한 결과 조회 = Query
        // Query query = em.createQuery("select m.id, m.name, m.age from Member m");

        List<Member> resultList = query.getResultList();

        for (Member member1 : resultList) {
            System.out.println("member = " + member1.getName() + ", team = " + member1.getTeam().getName());
        }
    }


    /**
     * 결과 받기
     *  1건:  getSingleResult()
     *  다건: getResultList()
     */
    public static void getResult(EntityManager em) throws Exception {

        Team team = em.find(Team.class, 7L);

        Member member = new Member();
        member.setName("NEW");
        member.setAge(20);
        member.getInTeam(team);
        em.persist(member);

        // 단건 조회 => SingleResult
        // 주의, JPA의 SingleResult는 무조건 결과가 1개여야함. (결과가 없거나 2개 이상이면 EXCEPTION 발생)
        // Spring Data JPA 에서는 결과가 없는 경우에는 빈 객체를 return해서 문제는 없음
        Member singleResult = em.createQuery("select m from Member m where m.name = :username", Member.class)
                                .setParameter("username", "NEW")
                                .getSingleResult();

        System.out.println("singleResult >> member = " + singleResult.getName());

        System.out.println("==========================================");

        // 다건 조회 => getResultList
        List<Member> resultList = em.createQuery("select m from Member m where m.team = :team", Member.class)
                                    .setParameter("team", team).getResultList();

        for (Member member1 : resultList) {
            System.out.println("resultList >>  member = " + member1.getName() + " team = " + member1.getTeam().getName());
        }
    }

    /**
     * jpql의 프로젝션
     *  - SELECT 절에서 조회할 대상을 지정하는 것
     *  - 프로젝션 대상: Entity, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입)
     * */
    public static void projection(EntityManager em) {

        // entity 프로젝션
        List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();

        // entity 프로젝션
        List<Team> resultList2 = em.createQuery("select t from Member m join m.team t", Team.class).getResultList();

        for (Team team : resultList2) {
            System.out.println("team id)name = " + team.getId() + ")" + team.getName());
        }

        // 임베디드 타입
        List<Address> resultList1 = em.createQuery("select o.address from Order o", Address.class).getResultList();

        // 스칼라 타입 (Object[] 타입으로 조회)
        List<Object[]> resultList3 = em.createQuery("select m.id, m.name from Member m").getResultList();

        for (Object[] objects : resultList3) {
            System.out.println("> member ==> " + objects[0] + ") " + objects[1]);
        }

        // 스칼라 타입 (new 명령어로 조회)
        // 단순 값을 dto로 바로 조회. 단, 패키지 명을 포함한 전체 클래스 명을 입력
        // 순서와 타입이 일치하는 생성자 필요
        List<MemberDto> resultList4 = em.createQuery("select new jpql.dto.MemberDto(m.id, m.name) from Member m", MemberDto.class).getResultList();

        // distinct
        List<MemberDto> resultList5 = em.createQuery("select distinct new jpql.dto.MemberDto(m.name) from Member m", MemberDto.class)
                                        .getResultList();

        for (MemberDto memberDto : resultList5) {
            System.out.println(">> member ==> " + memberDto.getName());
        }
    }
}
