package ext.requirement;

import java.util.List;

public class Requirement {
	private String number;
	private String name;
	private List<Post> posts;

	public Requirement() {
		super();
	}

	public Requirement(String number, String name, List<Post> posts) {
		super();
		this.number = number;
		this.name = name;
		this.posts = posts;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		return "Requirement [number=" + number + ", name=" + name + ", posts=" + posts.toString() + "]";
	}

	public static class Post {
		private PostData data;
		private int id;

		public Post() {
			super();
		}

		public Post(PostData data, int id) {
			super();
			this.data = data;
			this.id = id;
		}

		public PostData getData() {
			return data;
		}

		public void setData(PostData data) {
			this.data = data;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Post [data=" + data.toString() + ", id=" + id + "]";
		}

	}

	public static class PostData {
		private String imgs;

		public PostData() {
			super();
		}

		public PostData(String imgs) {
			super();
			this.imgs = imgs;
		}

		public String getImgs() {
			return imgs;
		}

		public void setImgs(String imgs) {
			this.imgs = imgs;
		}

		@Override
		public String toString() {
			return "PostData [imgs=" + imgs + "]";
		}

	}

}
