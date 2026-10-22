package com.milktea.order.mapper;

import com.milktea.model.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 新增订单明细(商品名/规格/价格均为下单时快照)
     * 注意: 实体字段 price 对应表列 unit_price, specs 对应 spec_info
     */
    @Insert("INSERT INTO order_detail(order_id, product_id, product_name, quantity, unit_price, spec_info, subtotal) " +
            "VALUES(#{orderId}, #{productId}, #{productName}, #{quantity}, #{price}, #{specs}, #{subtotal})")
    void insert(OrderDetail detail);

    @Select("SELECT id, order_id, product_id, product_name, quantity, unit_price AS price, " +
            "spec_info AS specs, subtotal FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> selectByOrderId(@Param("orderId") Long orderId);

    /** 商品销量排行TOP N(按商品名聚合明细) */
    @Select("SELECT product_name AS name, SUM(quantity) AS sales FROM order_detail " +
            "GROUP BY product_name ORDER BY sales DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> salesRank(@Param("limit") int limit);
}
