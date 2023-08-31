package uk.ac.wlv.blogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.List;
import java.util.UUID;

public class BlogPagerActivity extends AppCompatActivity {
    public static final String EXTRA_BLOG_ID = "uk.ac.wlv.blogapp.blog_id";
    private ViewPager mViewPager;
    private List<Blog> mBlogs;

    public static Intent newIntent(Context packageContext, UUID blogId) {
        Intent intent = new Intent(packageContext, BlogPagerActivity.class);
        intent.putExtra(EXTRA_BLOG_ID, blogId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_pager);
        UUID blogId = (UUID) getIntent().getSerializableExtra(EXTRA_BLOG_ID);
        mViewPager = findViewById(R.id.activity_blog_view_pager);
        mBlogs = BlogPost.get(this).getBlogs();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Blog blog = mBlogs.get(position);
                return BlogFragment.newInstance(blog.getId());
            }

            @Override
            public int getCount() {

                return mBlogs.size();
            }
        });
        for(int i = 0; i < mBlogs.size(); i++) {
            if(mBlogs.get(i).getId().equals(blogId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
