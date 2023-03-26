package org.phial.baas.service.sql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.mybatisx.dal.generator.DDL;
import org.phial.baas.service.domain.entity.NamedEntity;

import java.util.List;

/**
 * 自定义SQL提供者
 *
 */
@Slf4j
public class CustomSQLBuilder {
    /**
     * @return
     * @throws Exception
     */
    public synchronized String databaseDDL() throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            List<String> ss = DDL.ddl(true, false,
                    NamedEntity.class.getPackage().getName());
            if (CollectionUtils.isNotEmpty(ss)) {
                ss.forEach(e -> sb.append(e).append("\n"));
            }
        } catch (Exception e) {
            log.error("Generate DDL error", e);
        } finally {
            log.info("Generate database DDL success!");
        }
        String sql = sb.toString();
        if (StringUtils.isBlank(sql)) {
            return "CREATE TABLE IF NOT EXISTS `t_test` (\n" +
                    "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        }
        return sb.toString();
    }

    public String getTransactionReleaseFreezeItemWorkTaskIdByOrgId(Long orgId) {
        return String.format("SELECT distinct(workTaskId) workTaskId FROM t_iot_transaction_release_freeze_item sc " +
                "left join t_iot_goods ig on ig.id = sc.goodsId " +
                "left join t_iot_transaction_release_freeze_item_goods scg on sc.id = scg.itemId " +
                "where ig.pledgeOrgId = " + String.valueOf(orgId) + " or scg.pledgeOrgId = " + String.valueOf(orgId));
    }
}
