package uk.ac.wlv.blogapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class BlogListFragment extends Fragment {
    private RecyclerView mBlogRecyclerView;
    private BlogAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceSate) {
        View view = inflater.inflate(R.layout.fragment_blog_list, container, false);
        mBlogRecyclerView = view.findViewById(R.id.blog_recycler_view);
        mBlogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_blog_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_blog:
                Blog blog = new Blog();
                BlogPost.get(getActivity()).addBlog(blog);
                Intent intent = BlogPagerActivity
                        .newIntent(getActivity(), blog.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        BlogPost blogPost = BlogPost.get(getActivity());
        List<Blog> blogs = blogPost.getBlogs();
        if (mAdapter == null) {
            mAdapter = new BlogAdapter(blogs);
            mBlogRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setBlogs(blogs);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class BlogHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mCompleteCheckBox;
        private Blog mBlog;

        public BlogHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.list_item_blog_title_text_view);
            mDateTextView = itemView.findViewById(R.id.list_item_title_date_text_view);
            mCompleteCheckBox = itemView.findViewById(R.id.list_item_blog_complete_check_box);

        }

        @Override
        public void onClick(View v) {
            String blogToastID = mBlog.getTitle();
            Toast.makeText(getActivity(), blogToastID, Toast.LENGTH_SHORT).show();
            Intent intent = BlogPagerActivity.newIntent(getActivity(), mBlog.getId());
            startActivity(intent);
        }

        public void bindBlog(Blog blog) {
            mBlog = blog;
            mTitleTextView.setText(mBlog.getTitle());
            String pattern = "dd MMM, yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(mBlog.getDate());
            mDateTextView.setText(date);
            mCompleteCheckBox.setChecked(mBlog.isComplete());
            mCompleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mBlog.setComplete(isChecked);
                    BlogPost.get(getActivity()).updateBlog(mBlog);
                    mCompleteCheckBox.setChecked(mBlog.isComplete());
                }
            });
        }
    }


    private class BlogAdapter extends RecyclerView.Adapter<BlogHolder> {
        private List<Blog> mBlog;

        public void setBlogs(List<Blog> blogs) {
            mBlog = blogs;
        }

        public BlogAdapter(List<Blog> blogs) {
            mBlog = blogs;
        }

        @Override
        public BlogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_blog, parent, false);
            return new BlogHolder(view);
        }

        @Override
        public void onBindViewHolder(BlogHolder holder, int position) {
            Blog blog = mBlog.get(position);
            holder.bindBlog(blog);
        }

        @Override
        public int getItemCount() {
            return mBlog.size();
        }

    }
}
