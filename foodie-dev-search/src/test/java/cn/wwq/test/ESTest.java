package cn.wwq.test;

import cn.wwq.Application;
import cn.wwq.pojo.Items;
import cn.wwq.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 不建议使用ElasticsearchTemplate 对索引进行管理【增、删、改】
     * 索引就像是数据库，或者表。。
     * 只会对数据做CRUD操作【对文档数据使用ElasticsearchTemplate】
     */

    @Test
    public void createIndexStu(){

        Stu stu = new Stu();
        stu.setStuId(1003l);
        stu.setName("bb man");
        stu.setAge(66);
        stu.setMoney(66);
        stu.setDescription("bb man");
        stu.setSign("1");
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    @Test
    public void deleteIndexStu(){
        esTemplate.deleteIndex(Stu.class);
    }

    // ------------------------------

    @Test
    public void updateStuDoc(){

        HashMap<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("sign","I am not man");
        sourceMap.put("money",88);
        sourceMap.put("age",30);



        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("1002")
                .withIndexRequest(indexRequest)
                .build();
        esTemplate.update(updateQuery);
    }

    @Test
    public void getIndexStu(){
        GetQuery getQuery = new GetQuery();
        getQuery.setId("1002");
        Stu stu = esTemplate.queryForObject(getQuery, Stu.class);
        System.out.println(stu);
    }

    @Test
    public void deleteStuDoc(){
        esTemplate.delete(Stu.class,"1002");
    }

    @Test
    public void searchStuDoc(){

        Pageable  pageable = PageRequest.of(0, 10);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("itemName", "好吃"))
                .withPageable(pageable)
                .build();

        AggregatedPage<Items> items = esTemplate.queryForPage(searchQuery, Items.class);

        System.out.println("总分页数目"+items.getTotalPages());
        for (Items item : items.getContent()) {
            System.out.println(item);
        }
    }

    @Test
    public void hignlightStuDoc(){

        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable  pageable = PageRequest.of(0, 10);
        SortBuilder sortBuilder = new FieldSortBuilder("money")
                .order(SortOrder.DESC);
        SortBuilder ageBuilder = new FieldSortBuilder("age")
                .order(SortOrder.DESC);


        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "bb man"))
                .withHighlightFields(new HighlightBuilder.Field("description")
                                    .preTags(preTag)
                                    .postTags(postTag))
                .withPageable(pageable)
                .withSort(sortBuilder)
                .withSort(ageBuilder)
                .build();

        AggregatedPage<Stu> stus = esTemplate.queryForPage(searchQuery, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<Stu> stuList = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();

                for (SearchHit hit : hits) {
                    HighlightField description = hit.getHighlightFields().get("description");
                    String desc = description.getFragments()[0].toString();

                    Object stuId = (Object) hit.getSourceAsMap().get("stuId");
                    String sign = (String) hit.getSourceAsMap().get("sign");
                    String name = (String) hit.getSourceAsMap().get("name");
                    Integer age = (Integer) hit.getSourceAsMap().get("age");
                    Object money = (Object) hit.getSourceAsMap().get("money");
                    Stu stu = new Stu();
                    stu.setDescription(desc);
                    stu.setSign(sign);
                    stu.setName(name);
                    stu.setStuId(Long.valueOf(stuId.toString()));
                    stu.setAge(age);
                    stu.setMoney(Integer.valueOf(money.toString()));
                    stuList.add(stu);
                }

                if (null != stuList && stuList.size() > 0){
                    return new AggregatedPageImpl<>((List<T>)stuList);

                }
                return null;
            }
        });

        System.out.println("总分页数目"+stus.getTotalPages());
        for (Stu stu : stus.getContent()) {
            System.out.println(stu);
        }
    }




}
















