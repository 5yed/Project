package uk.ac.rhul.cs2810;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.Orders;
import uk.ac.rhul.cs2810.service.OrderService;

@SpringBootApplication
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

  @Bean
  public CommandLineRunner seedOrders(OrderService orderService, JdbcTemplate jdbc) {
    return args -> {
      String tableName = resolveRestaurantTableName(jdbc);

      Long count = jdbc.queryForObject("select count(*) from " + tableName, Long.class);
      if (count == null) count = 0L;

      if (count < 50) {
        Integer maxTableNum = jdbc.queryForObject(
            "select coalesce(max(table_number), 0) from " + tableName, Integer.class);
        int start = (maxTableNum == null ? 0 : maxTableNum) + 1;

        for (int i = 0; i < (50 - count); i++) {
          jdbc.update("insert into " + tableName + " (table_number) values (?)", start + i);
        }
      }

      List<Long> tableIds = jdbc.queryForList(
          "select id from " + tableName + " order by id", Long.class);

      int created = 0;
      for (Long tableId : tableIds) {
        if (created >= 10) break;

        try {
          Orders o = orderService.createOrder(tableId);
          orderService.updateStatus(o.getId(), OrderStatus.PLACED);
          created++;
        } catch (Exception e) {
          System.out.println("Seed order skipped for tableId=" + tableId + " reason=" + e.getMessage());
        }
      }

      System.out.println("Seeded orders: " + created);
    };
  }

  private static String resolveRestaurantTableName(JdbcTemplate jdbc) {
    try {
      jdbc.queryForObject("select count(*) from RESTAURANT_TABLE", Long.class);
      return "RESTAURANT_TABLE";
    } catch (BadSqlGrammarException ex) {
      return "restaurant_table";
    }
  }
}