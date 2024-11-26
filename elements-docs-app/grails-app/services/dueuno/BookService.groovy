/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno

class BookService {

    private static final data = [
            [id: '1', title: 'The Teachings of Don Juan', author: 'Carlos Castaneda', description: 'This is a nice fictional book'],
            [id: '2', title: 'The Antipodes of the Mind', author: 'Benny Shanon', description: 'This is a nice scientific book'],
    ]

    List<Map> list() {
        return data
    }

    Map get(Serializable id) {
        return data.find { it.id == id }
    }

    Map getByTitle(String title) {
        return data.find { it.title == title }
    }

    void create(Map record) {
        record.id = data.size() + 1
        data.add(record)
    }

    void update(Map record) {
        if (!record.id) throw new Exception("'id' required to update a record!")
        Map item = data.find { it.id == record.id }
        if (item) {
            item.title == record.title
            item.author = record.author
        }
    }

    void delete(Serializable id) {
        data.removeAll { it.id == id }
    }
}
