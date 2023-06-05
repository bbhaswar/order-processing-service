package com.order.process.orderprcessingservice.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.order.process.orderprcessingservice.entity.Order;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PageableTypeAdapter extends TypeAdapter<PageImpl<Order>> {

    @Override
    public void write(JsonWriter out, PageImpl<Order> value) throws IOException {
        throw new UnsupportedOperationException("Writing PageImpl<Order> not supported");
    }

    @Override
    public PageImpl<Order> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            in.beginObject();
            Pageable pageable = null;
            List<Order> content = null;
            long total = 0;

            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case "pageable":
                        pageable = readPageable(in);
                        break;
                    case "content":
                        content = readContent(in);
                        break;
                    case "total":
                        total = in.nextLong();
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }

            in.endObject();

            if (pageable != null && content != null) {
                return new PageImpl<>(content, pageable, total);
            } else {
                return null;
            }
        }
    }

    private Pageable readPageable(JsonReader in) throws IOException {
        in.beginObject();
        int pageNumber = 0;
        int pageSize = 0;
        String sortProperty = null;
        Sort.Direction sortDirection = Sort.Direction.ASC;

        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "pageNumber":
                    pageNumber = in.nextInt();
                    break;
                case "pageSize":
                    pageSize = in.nextInt();
                    break;
                case "sort":
                    in.beginObject();
                    while (in.hasNext()) {
                        String sortName = in.nextName();
                        if (sortName.equals("sorted") && in.nextBoolean()) {
                            String order = in.nextString();
                            sortDirection = Sort.Direction.fromString(order);
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();

        if (pageNumber >= 0 && pageSize > 0) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortProperty));
        } else {
            return null;
        }
    }

    private List<Order> readContent(JsonReader in) throws IOException {
        in.beginArray();
        List<Order> content = new ArrayList<>();

        while (in.hasNext()) {
            content.add(readOrder(in));
        }

        in.endArray();

        return content;
    }

    private Order readOrder(JsonReader in) throws IOException {
        in.beginObject();
        Long id = null;
        String email = null;
        String phoneNumber = null;
        Double parcelWeight = null;
        String country = null;
        String creationDate = null;

        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "id":
                    id = in.nextLong();
                    break;
                case "email":
                    email = in.nextString();
                    break;
                case "phoneNumber":
                    phoneNumber = in.nextString();
                    break;
                case "parcelWeight":
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                    } else {
                        parcelWeight = in.nextDouble();
                    }
                    break;
                case "country":
                    country = in.nextString();
                    break;
                case "creationDate":
                    creationDate = in.nextString();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();

        return Order.builder()
                .id(id)
                .email(email)
                .phoneNumber(phoneNumber)
                .parcelWeight(parcelWeight)
                .country(country)
                .creationDate(LocalDate.parse(creationDate))
                .build();
    }
}

