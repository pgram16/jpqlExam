package jpql;

import javax.persistence.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();



        tx.begin();

        try {

            basicExam(em);

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
     * jpql 쿼리 생성, 결과 조회, 파라미터 set
     * */
    public static void basicExam(EntityManager em) throws Exception {
        Team team = new Team();
        team.setName("NewTeam");
        em.persist(team);

            /*
            Member member = new Member();
            member.setName("AAA");
            member.setAge(20);
            member.getInTeam(team);
            em.persist(member);
            */

        Member member2 = new Member();
        member2.setName("AKF");
        member2.setAge(18);
        member2.getInTeam(team);
        em.persist(member2);

        // 반환타입이 명확한 결과 조회 = TypedQuery
        TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
        List<Member> resultList = query.getResultList();

        for (Member member1 : resultList) {
            System.out.println("member = " + member1.getName() + ", team = " + member1.getTeam().getName());
        }

        // 반환 타입이 불명확한 결과 조회 = Query
        Query query1 = em.createQuery("select m.id, m.name, m.age from Member m");

        System.out.println("===========");

        // 단건 조회의 경우 SingleResult 로 받는다
        // 주의, JPA의 SingleResult는 무조건 결과가 1개여야함. (결과가 없거나 2개 이상이면 EXCEPTION 발생)
        // Spring Data JPA 에서는 결과가 없는 경우에는 빈 객체를 return해서 문제는 없음
        Member singleResult = em.createQuery("select m from Member m where m.name = :username", Member.class)
                .setParameter("username", "AAA")
                .getSingleResult();
        /*
         * 실행 결과 NonUniqueResultException 발생한 원인
         * 이미 DB에 AAA라는 name 데이터가 존재, 위에서 또 AAA라는 name의 데이터를 추가함
         * JPQL은 FlushModeType.AUTO(기본) 모드일때는 실행 전 flush가 됨(영속성 컨텍스트의 1차 캐시 내용이 DB에 반영)
         * flush 후 select 되므로 select에서는 결과가 2건이 조회되는 것
         * */


        System.out.println("singleResult >> member = "+ singleResult.getName());
    }
}
