package com.example.chimpshelf.ui.download;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.example.chimpshelf.databinding.FragmentDownloadBinding;

import java.io.IOException;

public class DownloadFragment extends Fragment {

    private FragmentDownloadBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DownloadViewModel downloadViewModel =
                new ViewModelProvider(this).get(DownloadViewModel.class);

        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.searchButton.setOnClickListener( v -> handleBookInput());

        final TextView textView = binding.textDownload;
        downloadViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleBookInput() {
        System.out.println("Handle book input");

        String book = binding.inputBook.getText().toString();
        System.out.println("Book: " + book);

        // Clear the input field
        binding.inputBook.setText("");

        // Check if book is empty
        if (book.isEmpty()) {
            System.out.println("Book is empty");
            book = "The Great Gatsby";
        }

        // Search for book
        String url = "https://annas-archive.org/search?q=" + book.replace(" ", "+");

        System.out.println("URL: " + url);

        Thread thread = new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url).get();
                for (Element link : doc.select("a")) {
                    String href = link.attr("href");
                    System.out.println("Found link: " + href);
                    if (href.contains("/md5/")) {
                        System.out.println("Found download link: " + href);

                        downloadBook(href);

                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });

        thread.start();


    }

    private void downloadBook(String url) {
        System.out.println("Download book");
        System.out.println("URL: " + url);

        url = "https://annas-archive.org" + url;

        String finalUrl = url;
        System.out.println("Sending user to download page");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
        startActivity(intent);

    }
}