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
