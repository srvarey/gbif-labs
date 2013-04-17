/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry.todo;



public class ValidationTest {

// public static class MockNodeService implements NodeService {
//
// @Override
// public UUID create(WritableNode entity) {
// // TODO Auto-generated method stub
// return null;
// }
//
// @Override
// public Node get(UUID key) {
// // TODO Auto-generated method stub
// return null;
// }
//
// @Override
// public PagingResponse<Node> list(Pageable page) {
// // TODO Auto-generated method stub
// return null;
// }
//
// @Override
// public void update(WritableNode entity) {
// // TODO Auto-generated method stub
// // return null;
// }
//
// @Override
// public void delete(UUID key) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public int addTag(UUID targetEntityKey, String value) {
// // TODO Auto-generated method stub
// return 0;
// }
//
// @Override
// public void deleteTag(UUID taggedEntityKey, int tagKey) {
// // TODO Auto-generated method stub
//
// }
//
// @Override
// public List<Tag> listTags(UUID taggedEntityKey, String owner) {
// // TODO Auto-generated method stub
// return null;
// }
//
// }
//
//
// public static void main(String[] args) {
// Module m = new AbstractModule() {
//
// @Override
// protected void configure() {
// bind(NodeService.class).to(MockNodeService.class);
// bind(NodeResourceOrig.class);
// }
// };
// Injector i = Guice.createInjector(m, new ValidationModule());
// try {
// NodeResourceOrig nr = i.getInstance(NodeResourceOrig.class);
// nr.create(new WritableNode());
// // nr.print(new O());
// } catch (ConstraintViolationException e) {
// e.printStackTrace();
// for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
// System.out.println(cv.getPropertyPath());
// System.out.println(cv.getMessage());
// System.out.println(cv.getMessageTemplate());
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
}
