multicp
=======
This is a utility to efficiently copy an entire directory tree to multiple destinations at once. Written in java for portability.
The implementation reads through the source file stream once and copies to multiple destinations in parallel.

usage
=======
Grab the bin folder and run "java multicp.multicp [source_dir] dest_dir [dest_dir dest_dir ...]
